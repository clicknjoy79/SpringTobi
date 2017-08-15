package springbook.learningtest.ioc.bean;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BeanC {
    List<String> nameList;
    Set<String> nameSet;
    Map<String, Integer> ages;
    Properties settings;
    List beans;
    public void setNameList(List<String> nameList) { this.nameList = nameList; }
    public void setNameSet(Set<String> nameSet) { this.nameSet = nameSet; }
    public void setAges(Map<String, Integer> ages) {this.ages  = ages;}
    public void setSettings(Properties settings) { this.settings = settings; }
    public void setBeans(List beans) { this.beans = beans; }

    public List<String> getNameList() {
        return nameList;
    }

    public Set<String> getNameSet() {
        return nameSet;
    }

    public Map<String, Integer> getAges() {
        return ages;
    }

    public Properties getSettings() {
        return settings;
    }

    public List getBeans() {
        return beans;
    }
}
