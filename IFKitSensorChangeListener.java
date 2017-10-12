/*
Hannah Duncan
CSCI 1302
I am sole author of the assignment.  I have not received a digital copy or printout of the solution from anyone; however I receive outside help from the following
 websites and people: Craig Duncan
 I have not given a digital copy or printout of my code to anyone; however, I discussed this problem with the following people: Craig Duncan
*/

import com.phidgets.event.SensorChangeListener;
import com.phidgets.event.SensorChangeEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;


public class IFKitSensorChangeListener implements SensorChangeListener{
    
    private SimonSays appFrame;
   
    // Creates a new instance of IFKitSensorChangeListener
    public IFKitSensorChangeListener(SimonSays appFrame)
    {
        this.appFrame = appFrame;
    }

    public void sensorChanged(SensorChangeEvent sensorChangeEvent)
    {
    	System.out.println("Sensor change event");
        appFrame.processSensorInput(sensorChangeEvent.getIndex(), sensorChangeEvent.getValue());
    }
    
}
