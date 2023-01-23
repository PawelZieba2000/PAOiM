package com.PAOiM;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="courses")
public class Course {
    @Id
    private int course_id;
    private String name;
    private String location;
    private String date;
    @ManyToOne
    private Leader leader;
    @ManyToMany
    private List<Participant> participants = new ArrayList<Participant>();
    private int free_slots;
    private int price;

    public Course(int course_id, String name, String location, String date, /* Leader leader,*/ int free_slots, int price) {
        this.course_id = course_id;
        this.name = name;
        this.location = location;
        this.date = date;
        //this.leader = leader;
        this.free_slots = free_slots;
        this.price = price;
    }

    public int getId() {
        return course_id;
    }
    public void setId(int course_id) {
        this.course_id = course_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public int getFree_slots() {
        return free_slots;
    }
    public void setFree_slots(int free_slots) {
        this.free_slots = free_slots;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public Leader getLeader() {
        return leader;
    }
    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return
                "Course id: " + course_id +
                ", name: " + name +
                ", location: " + location  +
                ", date: " + date  + leader +
                ", free slots: " + free_slots +
                ", price: " + price;
    }
}
