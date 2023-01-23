package com.PAOiM;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server
{
    private ServerSocket server_socket;
    private Socket socket;
    private int my_port;
    private int n_threads;
    private Configuration con;
    private ServiceRegistry reg;
    private SessionFactory sf;
    private Session session;
    private Transaction tx;
    private ArrayList<AdminClientHandler> clients;
    private ExecutorService pool;

    public static void main( String[] args )
    {
//        Course test = new Course(1,"testowy","gdzies","kiedys",new Leader(1,"adam","malysz"),10,10000);
//        Configuration con = new Configuration().configure().addAnnotatedClass(Course.class).addAnnotatedClass(Leader.class).addAnnotatedClass(Participant.class).addAnnotatedClass(Reservation.class);
//        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(con.getProperties()).buildServiceRegistry();
//        SessionFactory sf = con.buildSessionFactory(reg);
//        Session session = sf.openSession();
//        Transaction tx = session.beginTransaction();
//        session.save(test);
//        tx.commit();
        Server server = new Server();
        server.serverApp();
    }

    public void serverApp() {
        connectToDB();
        waitForClients();
    }

    public void connectToDB(){
        con = new Configuration().configure().addAnnotatedClass(Course.class).addAnnotatedClass(Leader.class).addAnnotatedClass(Participant.class);//.addAnnotatedClass(Reservation.class);
        reg = new ServiceRegistryBuilder().applySettings(con.getProperties()).buildServiceRegistry();
        sf = con.buildSessionFactory(reg);
        session = sf.openSession();
        tx = session.beginTransaction();
        Leader Adam = new Leader(1, "Adam");
        Leader Janek = new Leader(2,"Jan");
        Leader Pawel = new Leader(3, "Pawel");
        Course test1 = new Course(1,"ogniska","Bieszczady","1.12.2023",10,10000);
        Course test2 = new Course(2,"gory","Gorce","23.10.2023",10,10000);
        Course test3 = new Course(3,"szlaki","Beskid zywiecki","11.11.2023",10,10000);
        Course test4 = new Course(4,"szlaki2","Beskid zywiecki","12.11.2023",10,10000);
        Course test5 = new Course(5,"szlaki3","Beskid zywiecki","13.11.2023",10,10000);
        Course test6 = new Course(6,"szlaki4","Beskid zywiecki","14.11.2023",10,10000);
        Participant participant = new Participant(1,"Pawel");
        test1.setLeader(Adam);
        test2.setLeader(Janek);
        test3.setLeader(Pawel);
        test4.setLeader(Adam);
        test5.setLeader(Janek);
        test6.setLeader(Pawel);
        Adam.getCourses().add(test1);
        Janek.getCourses().add(test2);
        Pawel.getCourses().add(test3);
        Adam.getCourses().add(test4);
        Janek.getCourses().add(test5);
        Pawel.getCourses().add(test6);
        session.save(participant);
        session.save(Adam);
        session.save(Pawel);
        session.save(Janek);
        session.save(test1);
        session.save(test2);
        session.save(test3);
        session.save(test4);
        session.save(test5);
        session.save(test6);
        tx.commit();
    }

    public Server() {
        try {
            my_port = 9999;
            n_threads = 10;
            server_socket = new ServerSocket(my_port);
            clients = new ArrayList<>();
            pool = Executors.newFixedThreadPool(n_threads);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForClients(){
        try {
            while (true) {
                System.out.println("Server is waiting for new clients");
                socket = server_socket.accept();
                System.out.println("New client just connect");
                AdminClientHandler client_thread = new AdminClientHandler(socket);
                clients.add(client_thread);
                pool.execute(client_thread);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ServerSocket getServer_socket() {
        return server_socket;
    }
    public void setServer_socket(ServerSocket server_socket) {
        this.server_socket = server_socket;
    }

    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getMy_port() {
        return my_port;
    }
    public void setMy_port(int my_port) {
        this.my_port = my_port;
    }

    public class AdminClientHandler implements Runnable{
        private Socket client_socket;
        private BufferedReader input;
        private PrintWriter out;
        private int request_for_server;

        public AdminClientHandler(Socket socket) throws IOException {
            this.client_socket = socket;
            input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        }

        @Override
        public void run() {
            int request_for_server = 0;
            int client_id = 0;
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            out.println("Give me your ID!");
            try {
                client_id = Integer.parseInt(br.readLine());
                if(session.get(Participant.class,client_id)==null){
                    //System.out.println("BRAK");
                    out.println(0);
                    String name = br.readLine();
                    Participant new_client = new Participant(client_id,name);
                    tx = session.beginTransaction();
                    session.save(new_client);
                    tx.commit();
                    System.out.println("zapisane");
                }
                else out.println(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true){
                    //listen from clients
                    try {
                        while(request_for_server!=4){

                            System.out.println("Server is waiting for requests");
                            request_for_server = Integer.parseInt(br.readLine());
                            System.out.println("request from client " + client_id +" : " + request_for_server);
                            //actions what to do
                            switch (request_for_server) {
                                //display courses
                                case 1:
                                    List<Course> fetched_courses = session.createCriteria(Course.class).list();
                                    out.println(fetched_courses.size());
                                    for (int i = 0 ;i < fetched_courses.size(); i ++){
                                        System.out.println(fetched_courses.get(i));
                                        out.println(fetched_courses.get(i).toString());
                                    }
                                    break;

                                //make reservation
                                case 2:
                                    int course_id = Integer.parseInt(br.readLine());
                                    if(session.get(Course.class,course_id) != null){
                                        Course tmp = (Course) session.get(Course.class,course_id);
                                        if(tmp.getFree_slots()>0){
                                            boolean is_already_done = false;
                                            for(int i = 0 ; i < tmp.getParticipants().size();i++){
                                                if(tmp.getParticipants().get(i).getParticipant_id()==client_id) is_already_done = true;
                                            }
                                            if(!is_already_done){
                                                tmp.setFree_slots(tmp.getFree_slots()-1);
                                                tmp.getParticipants().add((Participant) session.get(Participant.class,client_id));
                                                tx = session.beginTransaction();
                                                session.saveOrUpdate(tmp);
                                                tx.commit();
                                                out.println(102); // reservation done
                                            }
                                            else out.println(103); //reservation already done
                                        }
                                        else out.println(101); // no free slots
                                    }
                                    else out.println(100); //course doesn't exist
                                    break;

                                //delete reservation
                                case 3:
                                    int course_id2 = Integer.parseInt(br.readLine());
                                    if(session.get(Course.class,course_id2) != null){
                                        Course tmp = (Course) session.get(Course.class,course_id2);
                                        boolean good_course = false;
                                        for(int i = 0 ; i < tmp.getParticipants().size();i++){
                                            if(tmp.getParticipants().get(i).getParticipant_id()==client_id) good_course = true;
                                        }

                                        if(good_course) {
                                            tmp.setFree_slots(tmp.getFree_slots()+1);
                                            tmp.getParticipants().remove((Participant) session.get(Participant.class,client_id));
                                            tx = session.beginTransaction();
                                            session.saveOrUpdate(tmp);
                                            tx.commit();
                                            out.println(104); // delete git
                                        }
                                        else out.println(100);

                                    }
                                    else out.println(100); //course doesn't exist
                                    break;

                                //close connection
                                case 4:
                                    try {
                                        input.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                            }
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
        }
    }
}
