package com.PAOiM;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientGUI implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JTextField text_field;
    private JTextField reservationText;
    private JButton ok_button;
    private JButton reserv_butt;
    private JLabel greating;
    private JLabel action1, action2, action3, action4;
    private JLabel return_msg, reserv_label, result_label;
    private JScrollPane courses_panel;
    private int request_for_server, tmp_i, res_cours_id;
    private String server_ip_address;
    private int server_port, id;
    private Socket socket;
    private BufferedReader in;

    private OutputStreamWriter os;
    private PrintWriter out;

    public static void main(String[] args) {
        ClientGUI cg = new ClientGUI();
    }

    public ClientGUI(){
        request_for_server = 0;
        server_ip_address = "localhost";
        server_port = 9999;

        connectToServer();

        tmp_i=0;
        frame = new JFrame();
        panel = new JPanel();
        frame.setSize(800,800);
        frame.setTitle("Client application: Client id = " + id);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        greating = new JLabel("Hello client, what would you like to do? (Choose from options below)");
        action1 = new JLabel("1. Get list of available courses.");
        action2 = new JLabel("2. Make reservation for chosen course.");
        action4 = new JLabel("3. Delete reservation.");
        action3 = new JLabel("4. Exit.");
        courses_panel = new JScrollPane();
        return_msg = new JLabel("");

        greating.setBounds(10,20,400,40);
        action1.setBounds(10,35,400,40);
        action2.setBounds(10,50,400,40);
        action4.setBounds(10,65,400,40);
        action3.setBounds(10,80,400,40);
        return_msg.setBounds(10,95,400,40);

        text_field = new JTextField(10);
        text_field.setBounds(430,30,165,25);

        ok_button = new JButton("OK");
        ok_button.setBounds(600,30,60,25);
        ok_button.addActionListener(this);

        panel.add(greating);
        panel.add(action1);
        panel.add(action2);
        panel.add(action3);
        panel.add(action4);
        panel.add(text_field);
        panel.add(ok_button);
        panel.add(return_msg);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==ok_button){
            panel.removeAll();
            panel.add(greating);
            panel.add(action1);
            panel.add(action2);
            panel.add(action3);
            panel.add(action4);
            panel.add(text_field);
            panel.add(ok_button);
            panel.add(return_msg);

            request_for_server = Integer.parseInt(text_field.getText());
            text_field.setText("");
            if (request_for_server < 1 || request_for_server > 4) {
                panel.removeAll();
                panel.add(greating);
                panel.add(action1);
                panel.add(action2);
                panel.add(action3);
                panel.add(action4);
                panel.add(text_field);
                panel.add(ok_button);
                panel.add(return_msg);
                return_msg.setText("You chose wrong action, please choose again.");
            }
            else if(request_for_server==4) {
                Timer t = new Timer(100,ae->
                {
                    tmp_i++;
                    if(tmp_i==20) frame.dispose();
                    else return_msg.setText("You choose action " + request_for_server +". Goodbye!");
                });
                t.start();
            }
            else if(request_for_server == 1){
                panel.removeAll();
                panel.add(greating);
                panel.add(action1);
                panel.add(action2);
                panel.add(action3);
                panel.add(action4);
                panel.add(text_field);
                panel.add(ok_button);
                panel.add(return_msg);
                return_msg.setText("You choose action " + request_for_server);
                sendToServer(request_for_server);
                try {
                    int size = Integer.parseInt(in.readLine());
                    System.out.println(size);
                    List<String> courses = new ArrayList<>();
                    for(int i = 0; i < size; i ++) courses.add(in.readLine());
                    String[] tmp = courses.toArray(new String[0]);
                    JList<String> list = new JList<String>(tmp);
                    courses_panel = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    courses_panel.setBounds(10,130,760,400);
                    panel.add(courses_panel);
                    panel.revalidate();
                    panel.repaint();
                    //frame.setSize(803,803);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
            else if (request_for_server==2 || request_for_server==3){
                panel.removeAll();
                panel.add(greating);
                panel.add(action1);
                panel.add(action2);
                panel.add(action3);
                panel.add(action4);
                panel.add(text_field);
                panel.add(ok_button);
                panel.add(return_msg);
                panel.add(courses_panel);
                return_msg.setText("You choose action " + request_for_server);
                reservationText = new JTextField(10);
                reservationText.setBounds(430,650,165,25);
                panel.add(reservationText);

                reserv_butt = new JButton("OK");
                reserv_butt.setBounds(600,650,60,25);
                reserv_butt.addActionListener(this);
                panel.add(reserv_butt);

                if(request_for_server==2) reserv_label = new JLabel("Choose course_id for reservation:");
                if(request_for_server==3) reserv_label = new JLabel("Choose course_id for delete reservation:");
                reserv_label.setBounds(10,640,400,40);
                panel.add(reserv_label);
                panel.revalidate();
                panel.repaint();

                //frame.setSize(801,801);
            }
        }
        if(e.getSource()==reserv_butt){
            panel.removeAll();
            panel.add(greating);
            panel.add(action1);
            panel.add(action2);
            panel.add(action3);
            panel.add(action4);
            panel.add(text_field);
            panel.add(ok_button);
            panel.add(return_msg);
            res_cours_id = Integer.parseInt(reservationText.getText());
            System.out.println(res_cours_id);
            sendToServer(request_for_server);
            sendToServer(res_cours_id);
            try {
                int result = 0;
                result_label = new JLabel();
                result_label.setBounds(10,680,200,40);
                while (result == 0) {
                    result = Integer.parseInt(in.readLine());
                    if(result==100){
                        result_label.setText("Wrong ID");
                        panel.add(result_label);
                        panel.revalidate();
                        panel.repaint();
                        //frame.setSize(802,802);
                    }
                    else if(result == 101){
                        result_label.setText("No free slots");
                        panel.add(result_label);
                        panel.revalidate();
                        panel.repaint();
                        //frame.setSize(802,802);
                    }
                    else if (result==102) {
                        result_label.setText("reservation done");
                        panel.add(result_label);
                        panel.revalidate();
                        panel.repaint();
                        //frame.setSize(802,802);
                    }
                    else if (result==103) {
                        result_label.setText("reservation has already been done in the past");
                        panel.add(result_label);
                        panel.revalidate();
                        panel.repaint();
                        //frame.setSize(802,802);
                    }
                    else if (result==104) {
                        result_label.setText("reservation deleted");
                        panel.add(result_label);
                        panel.revalidate();
                        panel.repaint();
                        //frame.setSize(802,802);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void connectToServer() {
        try {
            socket = new Socket(server_ip_address, server_port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new OutputStreamWriter(socket.getOutputStream());
            out = new PrintWriter(os);
            System.out.println("C : successfully connected to server");
            System.out.println(in.readLine());
            id = Integer.parseInt(new Scanner(System.in).nextLine());
            System.out.println("Your id: " + id);
            out.println(Integer.toString(id));
            os.flush();
            if(Integer.parseInt(in.readLine())==0){
                System.out.println("Set name: ");
                String name = new Scanner(System.in).nextLine();
                out.println(name);
                os.flush();
            }
            else System.out.println("Good id");
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToServer(int request){
        try {
            out.println(request);
            os.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
