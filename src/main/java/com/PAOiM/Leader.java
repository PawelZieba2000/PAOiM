package com.PAOiM;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="leaders")
public class Leader {
    @Id
    private int leader_id;
    private String leader_name;
    @OneToMany(mappedBy = "leader", fetch = FetchType.EAGER)
    private List<Course> courses;

    public Leader(int leader_id, String name) {
        this.leader_id = leader_id;
        this.leader_name = name;
        courses = new ArrayList<Course>();
    }

    public int getLeader_id() {
        return leader_id;
    }
    public void setLeader_id(int leader_id) {
        this.leader_id = leader_id;
    }

    public String getLeader_name() {
        return leader_name;
    }
    public void setLeader_name(String leader_name) {
        this.leader_name = leader_name;
    }

    public List<Course> getCourses() {
        return courses;
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return
                "leader id: " + leader_id +
                ", name: " + leader_name;
    }
}
