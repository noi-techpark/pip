// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package it.bz.tis.alpenstaedte.dto;

import it.bz.tis.alpenstaedte.dto.Graph2Dto;
import it.bz.tis.alpenstaedte.dto.GraphTopicRootDto;
import java.util.List;

privileged aspect Graph2Dto_Roo_JavaBean {
    
    public List<GraphTopicRootDto> Graph2Dto.getChildren() {
        return this.children;
    }
    
    public void Graph2Dto.setChildren(List<GraphTopicRootDto> children) {
        this.children = children;
    }
    
    public String Graph2Dto.getName() {
        return this.name;
    }
    
    public void Graph2Dto.setName(String name) {
        this.name = name;
    }
    
}
