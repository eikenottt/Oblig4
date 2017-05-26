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
public class Debugger {

    DebugFrame debugFrame;

    public Debugger(){
        this.debugFrame = new DebugFrame("Debugger");
    }


    //Sends strings to the debugger
    public void print(String outstream){

        debugFrame.debugOut(outstream + "\n");

    }


    public DebugFrame getDebugFrame() {
        return debugFrame;
    }
}

