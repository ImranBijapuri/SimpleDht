package edu.buffalo.cse.cse486586.simpledht;

import java.io.Serializable;

/**
 * Created by imransay on 3/28/15.
 */
public class Joining_Node implements Serializable{
    public Joining_Node successor;
    public Joining_Node predecessor;
    public String port;
    public String node_id;
}
