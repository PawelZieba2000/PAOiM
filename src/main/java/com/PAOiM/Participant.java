package com.PAOiM;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    private int participant_id;
    private String participant_name;
    @ManyToMany(mappedBy = "participants")
    private List<Course> my_courses = new ArrayList<Course>();

    public Participant(){
        this.participant_name="";
        this.participant_id=0;
    }

    public Participant(int participant_id, String name) {
        this.participant_id = participant_id;
        this.participant_name = name;
    }

    public int getParticipant_id() {
        return participant_id;
    }
    public void setParticipant_id(int participant_id) {
        this.participant_id = participant_id;
    }

    public String getParticipant_name() {
        return participant_name;
    }
    public void setParticipant_name(String participant_name) {
        this.participant_name = participant_name;
    }

    public List<Course> getMy_courses() {
        return my_courses;
    }

    public void setMy_courses(List<Course> my_courses) {
        this.my_courses = my_courses;
    }

    @Override
    public String toString() {
        return
                "participant id: " + participant_id +
                ", name: " + participant_name;
    }
}
