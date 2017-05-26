package no.uib.info233.v2017.rei008_jsi014.oblig4;

import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.ButtonPanel;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.DebugFrame;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.MainFrame;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.MenuPanel;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import javax.swing.*;

/**
 * Debugger class handles the communications between the program and the Debugging console.
 * Created by John Tore on 25.05.2017.
 */
public final class Debugger {

    static DebugFrame debugFrame;
    static Debugger debugger;

    public Debugger(){
        debugFrame = new DebugFrame("Debugger");
    }

    static{
        try{
            debugger = new Debugger();
        }catch(Exception e){
            throw new RuntimeException("Exception in creating Debugger singleton instance");
        }
    }


    //Sends strings to the debugger
    public static void print(String outstream){

        debugFrame.debugOut(outstream + "\n");

    }


    public static DebugFrame getDebugFrame() {
        return debugFrame;
    }
}

