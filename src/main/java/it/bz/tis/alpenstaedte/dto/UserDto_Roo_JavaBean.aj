// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package it.bz.tis.alpenstaedte.dto;

import it.bz.tis.alpenstaedte.dto.OrganisazionDto;
import it.bz.tis.alpenstaedte.dto.TopicDto;
import it.bz.tis.alpenstaedte.dto.UserDto;
import java.util.Set;

privileged aspect UserDto_Roo_JavaBean {
    
    public String UserDto.getEmail() {
        return this.email;
    }
    
    public void UserDto.setEmail(String email) {
        this.email = email;
    }
    
    public Set<OrganisazionDto> UserDto.getOrganizations() {
        return this.organizations;
    }
    
    public void UserDto.setOrganizations(Set<OrganisazionDto> organizations) {
        this.organizations = organizations;
    }
    
    public String UserDto.getName() {
        return this.name;
    }
    
    public void UserDto.setName(String name) {
        this.name = name;
    }
    
    public String UserDto.getSurname() {
        return this.surname;
    }
    
    public void UserDto.setSurname(String surname) {
        this.surname = surname;
    }
    
    public String UserDto.getPhone() {
        return this.phone;
    }
    
    public void UserDto.setPhone(String phone) {
        this.phone = phone;
    }
    
    public Set<TopicDto> UserDto.getTopics() {
        return this.topics;
    }
    
    public void UserDto.setTopics(Set<TopicDto> topics) {
        this.topics = topics;
    }
    
    public String UserDto.getUuid() {
        return this.uuid;
    }
    
    public void UserDto.setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public String UserDto.getRole() {
        return this.role;
    }
    
    public void UserDto.setRole(String role) {
        this.role = role;
    }
    
    public Set<String> UserDto.getLanguageSkills() {
        return this.languageSkills;
    }
    
    public void UserDto.setLanguageSkills(Set<String> languageSkills) {
        this.languageSkills = languageSkills;
    }
    
    public boolean UserDto.isActive() {
        return this.active;
    }
    
    public void UserDto.setActive(boolean active) {
        this.active = active;
    }
    
}
