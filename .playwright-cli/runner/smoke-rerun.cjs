const fs = require('fs');
const path = require('path');
const { chromium, request } = require('playwright');

const root = path.resolve(__dirname, '..');
const outFile = path.join(root, 'smoke-result.json');
const runId = new Date().toISOString().replace(/[-:TZ.]/g, '').slice(0, 14);
const apiBase = 'http://localhost:8080';
const uiBase = 'http://127.0.0.1:5173';
const rows = [];
const ids = {};
const consoleErrors = [];
const requestFailures = [];

function short(value, limit = 520) {
  const text = typeof value === 'string' ? value : JSON.stringify(value);
  return text.length > limit ? text.slice(0, limit) + '...' : text;
}

function dataOf(body) {
  return body && typeof body === 'object' && Object.prototype.hasOwnProperty.call(body, 'data')
    ? body.data
    : body;
}

function idOf(body, text) {
  const match = text && text.match(/"id"\s*:\s*"?(\d+)"?/);
  if (match) return match[1];
  const data = dataOf(body);
  if (data == null) return undefined;
  if (typeof data === 'number' || typeof data === 'string') return data;
  return data.id ?? data.value;
}

function firstArrayId(text) {
  const match = text && text.match(/"data"\s*:\s*\[\s*\{\s*"id"\s*:\s*"?(\d+)"?/);
  return match ? match[1] : undefined;
}

function firstCrfAssessmentId(text) {
  const match = text && text.match(/"crfAssessments"\s*:\s*\[\s*\{\s*"id"\s*:\s*"?(\d+)"?/);
  return match ? match[1] : undefined;
}

function okHttp(status) {
  return status >= 200 && status < 300;
}

function add(group, name, method, pathValue, ok, detail, mismatch = false) {
  rows.push({ group, name, method, path: pathValue, ok: !!ok, detail: short(detail), mismatch: !!mismatch });
}

async function parse(resp) {
  const status = resp.status();
  const text = await resp.text();
  let body = null;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = text;
  }
  return { status, text, body };
}

async function api(ctx, group, name, method, urlPath, payload, expect = status => okHttp(status)) {
  try {
    const options = payload === undefined ? {} : { data: payload };
    const requestPath = urlPath.startsWith('/api/') ? urlPath : `/api${urlPath}`;
    let resp;
    if (method === 'GET') resp = await ctx.get(requestPath, options);
    else if (method === 'POST') resp = await ctx.post(requestPath, options);
    else if (method === 'PUT') resp = await ctx.put(requestPath, options);
    else throw new Error('unsupported method ' + method);
    const got = await parse(resp);
    const ok = expect(got.status, got.body);
    add(group, name, method, urlPath, ok, `HTTP ${got.status} ${short(got.text)}`);
    return { ok, ...got };
  } catch (error) {
    add(group, name, method, urlPath, false, error.stack || error.message);
    return { ok: false, error };
  }
}

function listOf(body) {
  const data = dataOf(body);
  if (Array.isArray(data)) return data;
  if (data && Array.isArray(data.content)) return data.content;
  return [];
}

async function runUiSmoke() {
  let browser;
  try {
    try {
      browser = await chromium.launch({ channel: 'chrome', headless: true });
    } catch {
      browser = await chromium.launch({ headless: true });
    }
    const page = await browser.newPage();
    page.on('console', msg => {
      if (msg.type() === 'error') consoleErrors.push(msg.text());
    });
    page.on('requestfailed', req => {
      requestFailures.push(`${req.method()} ${req.url()} ${req.failure()?.errorText || ''}`);
    });

    const cases = [
      ['首页', '/#/', '首页概览'],
      ['项目中心', '/#/projects', '项目中心'],
      ['数据导出', '/#/export-tasks', '导出任务列表'],
      ['统计分析', '/#/analysis', '分析项目'],
    ];

    for (const [name, route, expected] of cases) {
      try {
        const resp = await page.goto(uiBase + route, { waitUntil: 'domcontentloaded', timeout: 15000 });
        await page.waitForFunction(text => document.body.innerText.includes(text), expected, { timeout: 10000 });
        await page.waitForLoadState('networkidle', { timeout: 8000 }).catch(() => {});
        const bodyText = await page.locator('body').innerText({ timeout: 5000 });
        const hash = await page.evaluate(() => location.hash);
        add(
          'GET/UI',
          name,
          'GET',
          route,
          bodyText.includes(expected),
          `HTTP ${resp && resp.status()} hash=${hash} text=${short(bodyText.replace(/\s+/g, ' '), 180)}`,
        );
      } catch (error) {
        add('GET/UI', name, 'GET', route, false, error.message);
      }
    }
  } catch (error) {
    add('GET/UI', 'browser smoke setup', 'N/A', uiBase, false, error.stack || error.message);
  } finally {
    if (browser) await browser.close().catch(() => {});
  }
}

async function main() {
  await runUiSmoke();
  const ctx = await request.newContext({ baseURL: apiBase, extraHTTPHeaders: { Accept: 'application/json' } });

  await api(ctx, 'GET/API', 'list projects', 'GET', '/projects');
  await api(ctx, 'GET/API', 'list exports', 'GET', '/export-tasks');
  await api(ctx, 'GET/API', 'list analysis projects', 'GET', '/analysis/projects');

  const suffix = runId.slice(-6);
  let response = await api(ctx, '项目', 'create', 'POST', '/projects', {
    title: `PW项目-${runId}`,
    type: 'INTERVENTIONAL',
    abbreviation: `PW${suffix}`,
    prefix: `PW${suffix}`,
    openScreen: true,
    expectedSubjectSize: 12,
    clinicalNumber: `CLN-${runId}`,
    registrationNo: `REG-${runId}`,
    purpose: 'Playwright smoke rerun',
    createUserId: 'pw-runner',
  });
  ids.projectId = idOf(response.body, response.text);
  if (!ids.projectId) return;

  await api(ctx, '项目', 'get project after create', 'GET', `/projects/${ids.projectId}`);
  await api(ctx, '项目', 'add stage', 'POST', `/projects/${ids.projectId}/stages`, {
    name: `筛选期-${suffix}`,
    repeatType: 'NONE',
    autoAdd: true,
    baselineDays: 0,
    baselineDirection: 'DAY',
    beforeDays: 0,
    afterDays: 7,
  });
  let projectRead = await api(ctx, '项目', 'get project for stage ids', 'GET', `/projects/${ids.projectId}`);
  ids.stageId = (dataOf(projectRead.body)?.stages || [])[0]?.id;
  add('项目', 'read stage after add', 'GET', `/projects/${ids.projectId}`, !!ids.stageId, `stageId=${ids.stageId || 'none'}`);

  await api(ctx, '项目', 'add follow-up stage', 'POST', `/projects/${ids.projectId}/stages`, {
    name: `随访期-${suffix}`,
    repeatType: 'NONE',
    autoAdd: false,
    baselineDays: 7,
    baselineDirection: 'DAY',
    beforeDays: 1,
    afterDays: 7,
  });
  projectRead = await api(ctx, '项目', 'get project for visit stage ids', 'GET', `/projects/${ids.projectId}`);
  ids.stage2Id = (dataOf(projectRead.body)?.stages || [])[1]?.id;
  add('项目', 'read follow-up stage after add', 'GET', `/projects/${ids.projectId}`, !!ids.stage2Id, `stage2Id=${ids.stage2Id || 'none'}`);

  await api(ctx, '项目', 'assign personnel', 'POST', `/projects/${ids.projectId}/personnel`, { userId: 1, siteId: 1, role: 'CRC' });
  projectRead = await api(ctx, '项目', 'read personnel after assign', 'GET', `/projects/${ids.projectId}`);
  add('项目', 'personnel persisted', 'GET', `/projects/${ids.projectId}`, (dataOf(projectRead.body)?.sitePersonnel || []).length > 0, dataOf(projectRead.body)?.sitePersonnel || []);

  if (ids.stageId) {
    await api(ctx, '项目', 'add visit plan', 'POST', `/projects/${ids.projectId}/visit-plans`, {
      name: `访视-${suffix}`,
      sourceStageId: ids.stageId,
      targetStageId: ids.stage2Id || ids.stageId,
      baselineDays: 0,
      baselineDirection: 'DAY',
      beforeDays: 0,
      afterDays: 7,
      crfComponentId: 'smoke-crf',
    });
    await api(ctx, '项目', 'bind crf', 'POST', `/projects/${ids.projectId}/crf-bindings`, {
      stageId: ids.stageId,
      crfId: 1,
      crfVersionId: 1,
      userInputEnabled: true,
    });
    projectRead = await api(ctx, '项目', 'read crf binding after bind', 'GET', `/projects/${ids.projectId}`);
    add('项目', 'crf binding persisted', 'GET', `/projects/${ids.projectId}`, (dataOf(projectRead.body)?.crfBindings || []).length > 0, dataOf(projectRead.body)?.crfBindings || []);
  }

  await api(ctx, '项目', 'activate', 'POST', `/projects/${ids.projectId}/activate`);
  projectRead = await api(ctx, '项目', 'get project after activate', 'GET', `/projects/${ids.projectId}`);
  ids.projectStatus = dataOf(projectRead.body)?.status;
  add('项目', 'project status active', 'GET', `/projects/${ids.projectId}`, ids.projectStatus === 'ACTIVE', `status=${ids.projectStatus}`);

  response = await api(ctx, '受试者', 'direct enroll', 'POST', '/subjects/enroll', {
    projectId: ids.projectId,
    siteId: 1,
    userId: 1,
    blh: `BLH-${suffix}`,
    syxh: `SY-${suffix}`,
    groupSubsetIds: [],
  });
  ids.subjectId = idOf(response.body, response.text);
  await api(ctx, '受试者', 'list subjects after direct enroll', 'GET', `/projects/${ids.projectId}/subjects`);
  if (ids.subjectId) await api(ctx, '受试者', 'get direct enrolled subject', 'GET', `/subjects/${ids.subjectId}`);

  response = await api(ctx, '受试者', 'screen subject', 'POST', '/subjects/screen', {
    projectId: ids.projectId,
    siteId: 1,
    userId: 1,
    screeningDate: '2026-06-17',
    screeningResult: 'PASS',
    remarks: 'smoke screen',
    blh: `SBLH-${suffix}`,
    syxh: `SSY-${suffix}`,
  });
  ids.screenedSubjectId = idOf(response.body, response.text);
  if (ids.screenedSubjectId) {
    await api(ctx, '受试者', 'enroll screened subject', 'POST', `/subjects/${ids.screenedSubjectId}/enroll`, {
      projectId: ids.projectId,
      siteId: 1,
      userId: 1,
      blh: `SBLH-${suffix}`,
      syxh: `SSY-${suffix}`,
      groupSubsetIds: [],
    });
    await api(ctx, '受试者', 'change status', 'PUT', `/subjects/${ids.screenedSubjectId}/status`, { newStatus: 'ACTIVE', reason: 'smoke status' }, status => okHttp(status) || status === 400 || status === 422);
  }

  if (ids.subjectId) {
    response = await api(ctx, '评估', 'list subject stages', 'GET', `/subjects/${ids.subjectId}/stages`);
    const stages = listOf(response.body);
    ids.subjectStageId = firstArrayId(response.text) || stages[0]?.id;
    add('评估', 'auto generated subject stage', 'GET', `/subjects/${ids.subjectId}/stages`, !!ids.subjectStageId, `subjectStageId=${ids.subjectStageId || 'none'} count=${stages.length}`);
    if (ids.subjectStageId) {
      response = await api(ctx, '评估', 'get subject stage detail', 'GET', `/stages/${ids.subjectStageId}`);
      const assessments = dataOf(response.body)?.crfAssessments || dataOf(response.body)?.assessments || [];
      ids.assessmentId = firstCrfAssessmentId(response.text) || assessments[0]?.id;
      add('评估', 'auto generated assessment', 'GET', `/stages/${ids.subjectStageId}`, !!ids.assessmentId, `assessmentId=${ids.assessmentId || 'none'} count=${assessments.length}`);
    }
    if (ids.assessmentId) {
      await api(ctx, '评估', 'get assessment detail', 'GET', `/assessments/${ids.assessmentId}`);
      await api(ctx, '评估', 'save field value', 'POST', `/assessments/${ids.assessmentId}/field-values`, {
        fieldCode: 'SMOKE_FIELD',
        fieldLabel: 'Smoke Field',
        fieldValue: '42',
        fieldValueText: '42',
        dataUnit: '分',
        fieldType: 'NUMBER',
        subTableId: null,
        userId: 1,
      });
      response = await api(ctx, '评估', 'read assessment after save', 'GET', `/assessments/${ids.assessmentId}`);
      const fields = dataOf(response.body)?.fieldValues || [];
      add('评估', 'field value persisted', 'GET', `/assessments/${ids.assessmentId}`, fields.some(item => item.fieldCode === 'SMOKE_FIELD'), fields);

      response = await api(ctx, '质疑', 'raise query', 'POST', '/queries', {
        assessmentId: ids.assessmentId,
        fieldCode: 'SMOKE_FIELD',
        subTableId: null,
        fieldType: 'NUMBER',
        question: 'smoke query?',
        originalFieldCode: 'SMOKE_FIELD',
        originalFieldValue: '42',
        originalFieldValueText: '42',
        userId: 1,
      });
      ids.queryId = idOf(response.body, response.text);
      if (ids.queryId) {
        await api(ctx, '质疑', 'list queries after raise', 'GET', `/assessments/${ids.assessmentId}/queries`);
        await api(ctx, '质疑', 'respond query', 'POST', `/queries/${ids.queryId}/respond`, { response: 'smoke response', updateType: 'CLARIFY_ONLY', newFieldValue: null, newFieldValueText: null, userId: 1 });
        await api(ctx, '质疑', 'close query', 'POST', `/queries/${ids.queryId}/close`, { userId: 1 });
        await api(ctx, '评估', 'audit assessment', 'POST', `/assessments/${ids.assessmentId}/audit`, { userId: 1 });
        await api(ctx, '质疑', 'reopen query', 'POST', `/queries/${ids.queryId}/reopen`, { reason: 'smoke reopen', userId: 1 }, status => okHttp(status) || status === 400 || status === 422);
        await api(ctx, '质疑', 'get query final', 'GET', `/queries/${ids.queryId}`);
      }
    }
  }

  const exportPayload = { taskName: `导出A-${suffix}`, projectId: ids.projectId, stageId: ids.stageId || null, crfVersionId: 1, fileFormat: 'XLSX' };
  response = await api(ctx, '导出', 'create export task A', 'POST', '/export-tasks', exportPayload);
  ids.exportA = idOf(response.body, response.text);
  if (ids.exportA) {
    await api(ctx, '导出', 'submit export A', 'POST', `/export-tasks/${ids.exportA}/submit`);
    await api(ctx, '导出', 'get export A after submit', 'GET', `/export-tasks/${ids.exportA}`);
    await api(ctx, '导出', 'approve export A', 'POST', `/export-tasks/${ids.exportA}/approve`, { userId: 'pw-runner', message: 'approve smoke' });
    await api(ctx, '导出', 'get export A after approve', 'GET', `/export-tasks/${ids.exportA}`);
    await api(ctx, '导出', 'execute export A', 'POST', `/export-tasks/${ids.exportA}/execute`, undefined, status => okHttp(status) || status === 400 || status === 422);
    await api(ctx, '导出', 'get export A after execute', 'GET', `/export-tasks/${ids.exportA}`);
  }

  response = await api(ctx, '导出', 'create export task B', 'POST', '/export-tasks', { ...exportPayload, taskName: `导出B-${suffix}` });
  ids.exportB = idOf(response.body, response.text);
  if (ids.exportB) {
    await api(ctx, '导出', 'submit export B', 'POST', `/export-tasks/${ids.exportB}/submit`);
    await api(ctx, '导出', 'reject export B', 'POST', `/export-tasks/${ids.exportB}/reject`, { userId: 'pw-runner', reason: 'reject smoke' });
    await api(ctx, '导出', 'get export B after reject', 'GET', `/export-tasks/${ids.exportB}`);
  }

  response = await api(ctx, '分析', 'create analysis project', 'POST', '/analysis/projects', { name: `分析-${suffix}`, description: 'Playwright smoke rerun' });
  ids.analysisProjectId = idOf(response.body, response.text);
  if (ids.analysisProjectId) {
    response = await api(ctx, '分析', 'get analysis project', 'GET', `/analysis/projects/${ids.analysisProjectId}`);
    const configs = dataOf(response.body)?.analysisConfigs || [];
    ids.analysisConfigId = configs[0]?.id;
    if (ids.analysisConfigId) {
      await api(ctx, '分析', 'execute analysis', 'POST', `/analysis/projects/${ids.analysisProjectId}/execute`, { configId: ids.analysisConfigId });
    } else {
      await api(ctx, '分析', 'execute analysis without valid config', 'POST', `/analysis/projects/${ids.analysisProjectId}/execute`, { configId: 1 }, status => status >= 400 && status < 500);
    }
  }

  if (ids.screenedSubjectId) {
    await api(ctx, '受试者', 'withdraw screened subject', 'POST', `/subjects/${ids.screenedSubjectId}/withdraw`, { reasonCode: 'SMOKE', reasonDescription: 'smoke withdraw' }, status => okHttp(status) || status === 400 || status === 422);
  }
  await api(ctx, '项目', 'close project final', 'POST', `/projects/${ids.projectId}/close`, undefined, status => okHttp(status) || status === 400 || status === 422);
  await api(ctx, '项目', 'get project after close', 'GET', `/projects/${ids.projectId}`);

  await ctx.dispose();
}

main()
  .catch(error => {
    add('runner', 'fatal', 'N/A', 'runner', false, error.stack || error.message);
    process.exitCode = 1;
  })
  .finally(() => {
    const result = {
      runId,
      generatedAt: new Date().toISOString(),
      ids,
      consoleErrors,
      requestFailures,
      summary: {
        total: rows.length,
        passed: rows.filter(row => row.ok).length,
        failed: rows.filter(row => !row.ok).length,
      },
      rows,
    };
    fs.writeFileSync(outFile, JSON.stringify(result, null, 2));
    console.log(JSON.stringify(result.summary));
    console.log(outFile);
  });
