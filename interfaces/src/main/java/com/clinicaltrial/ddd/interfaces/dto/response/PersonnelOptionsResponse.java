package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.List;

public class PersonnelOptionsResponse {

    private List<OptionVo> users;
    private List<OptionVo> sites;

    public List<OptionVo> getUsers() { return users; }
    public void setUsers(List<OptionVo> users) { this.users = users; }
    public List<OptionVo> getSites() { return sites; }
    public void setSites(List<OptionVo> sites) { this.sites = sites; }

    public static class OptionVo {
        private Long id;
        private String label;

        public OptionVo() {
        }

        public OptionVo(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }
}
