/*
Hannah Duncan
CSCI 1302
I am sole author of the assignment.  I have not received a digital copy or printout of the solution from anyone; however I receive outside help from the following
 websites and people: Craig Duncan
 I have not given a digital copy or printout of my code to anyone; however, I discussed this problem with the following people: Craig Duncan
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.phidgets.*;
import com.phidgets.event.*;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import java.applet.*;
import java.net.*; 	 
import java.io.*;

public class SimonSays extends JFrame
{
   //title panel
   private JPanel titlePanel = new JPanel();
   private JLabel title = new JLabel("Welcome to Simon Says: Phidget Edition!");
   private JLabel instructions1 = new JLabel("Instructions: The goal of the game is like any electronic Simon Says game. The four lights of different colors will light up in a ");
   private JLabel instructions2 = new JLabel("random order. Your goal is to match that pattern. Each correct press of the touch panels earns you 5 points. The maximum");
   private JLabel instructions3 = new JLabel("point value you can receive is 75 points but you can get bonus points if you complete the game in a certain amount of time.");
   private JLabel instructions4 = new JLabel("Press the button when you're ready. Good luck!");
   
   private  int THRESHOLD = 500;
   //points and timer panel
   private JPanel ptPanel = new JPanel();
   
   private JPanel pointsPanel = new JPanel();
   private JLabel levelTitle = new JLabel("Levels + Points:");
   private JCheckBox lvl1 = new JCheckBox("Level 1: 5 points");
   private JCheckBox lvl2 = new JCheckBox("Level 2: 10 points");
   private JCheckBox lvl3 = new JCheckBox("Level 3: 15 points");
   private JCheckBox lvl4 = new JCheckBox("Level 4: 20 points");
   private JCheckBox lvl5 = new JCheckBox("Level 5: 25 points");
   
   private int lvlNum = 0;
   private JLabel levelNum = new JLabel("Level " + lvlNum);
   
   private JPanel pnlTimer = new JPanel();
   private Timer timer;
   private JLabel timerTitle = new JLabel("Time ");
   private int min = 0;
   private int sec = 0;
   private JLabel time = new JLabel(min+":"+sec);
   
   private JPanel timeBonusPanel = new JPanel();
   private JLabel bonusTitle = new JLabel("Time Bonuses:");
   private JRadioButton min1 = new JRadioButton("Under 0:30 : +100 pts");
   private JRadioButton min2 = new JRadioButton("Under 1:00 : +75 pts");
   private JRadioButton min3 = new JRadioButton("Under 1:30 : +50 pts");
   private JRadioButton min4 = new JRadioButton("Under 2:00 : +25 pts");
   private JRadioButton min5 = new JRadioButton("Under 2:30 : +10 pts");
   
   //color panel
   private JPanel colorPanel = new JPanel();
   private JPanel redPanel = new JPanel();
   private JPanel bluePanel = new JPanel();
   private JPanel greenPanel = new JPanel();
   private JPanel yellowPanel = new JPanel();
   
   //score panel
   private JPanel scorePanel = new JPanel();
   private int points = 0;
   private JLabel score = new JLabel("Score: " + points);
   
   private JPanel fullPanel = new JPanel();
   private ActionListener timerListener;
   
   //phidgets
   private TextLCDPhidget screen; 
   private InterfaceKitPhidget ik;
   private Random rand = new Random();
   private ArrayList<Integer> numColor = new ArrayList<Integer>();
   private ArrayList<Integer> input = new ArrayList<Integer>();
   private int buttonPressed = 0;
   private boolean buttonPushed = false;
   private IFKitSensorChangeListener sensor_listener;
   private int userLevel;
   private boolean gameOver = false;
   private AudioClip cheering;
   private AudioClip lost;
   
   public SimonSays() 
   {
      setSize(900,1000);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Simon Says");
      
      buildTitlePanel();
      buildPointsAndTimerPanel();
      buildColorPanel();
      buildScorePanel();
      
      fullPanel.setLayout(new GridLayout(3,1));
      add(fullPanel);
      
      ButtonGroup bonusGroup = new ButtonGroup();
      bonusGroup.add(min1);
      bonusGroup.add(min2);
      bonusGroup.add(min3);
      bonusGroup.add(min4);
      bonusGroup.add(min5);
      
      openPhidgets();
      sensor_listener = new IFKitSensorChangeListener(this);
      ik.addSensorChangeListener(sensor_listener); 
   }
   public void processSensorInput(int portVal, int val)
   {  
      System.out.println("processing Sensor input port: " + portVal + ", value: " + val);
      try{      
         int num = val; 
         int v = val;
         if(portVal == 0) 
         {           
            System.out.println("Value: " + val);
            if(v >= 0)
            {
               timer.start();
               buttonPressed++;
            }
            if(buttonPressed == 1)
            {
               buttonPressed = 2;
               generatePattern();
               System.out.println("Button pressed");
               buttonPushed = true;
               play();
            }
         }
      
         if (!buttonPushed) {
            return;
         }
         if(portVal == 4)
         {
            if(num >= THRESHOLD)
            {
               input.add(3);
               yellowPanel.setBackground(Color.YELLOW);
               System.out.println("Yellow");
               ik.setOutputState(0, true);               
            }
            if(num == 0)
            {
               yellowPanel.setBackground(null);
               ik.setOutputState(0, false);
            }
         }
         if(portVal == 5)
         {
            if(num >= THRESHOLD)
            {
               input.add(2);
               greenPanel.setBackground(Color.GREEN);
               System.out.println("Green");
               ik.setOutputState(1, true);
            }
            if(num == 0)
            {
               greenPanel.setBackground(null);
               ik.setOutputState(1, false);
            }
         }
         if(portVal == 6)
         {
            if(num >= THRESHOLD)
            {
               input.add(1);
               bluePanel.setBackground(Color.BLUE);
               System.out.println("Blue");
               ik.setOutputState(2, true);
            }
            if(num == 0)
            {
               bluePanel.setBackground(null);
               ik.setOutputState(2, false);
            }
         }
         if(portVal == 7)
         {
            if(num >= THRESHOLD)
            {
               input.add(0);
               redPanel.setBackground(Color.RED);
               System.out.println("Red");
               ik.setOutputState(3, true);
            }
            if(num == 0)
            {
               redPanel.setBackground(null);
               ik.setOutputState(3, false);
            }
         }
         if (input.size() >= userLevel) {
            List<Integer> actualValue = input.subList(0, userLevel);
            List<Integer> subList = numColor.subList(0, userLevel);
            System.out.println("\nExpected value");
            for (Integer integer : subList) {
               System.out.print(integer);
            }
            System.out.println("\nActual value");
            for (Integer integer : actualValue) {
               System.out.print(integer);
            }
         
            if (subList.equals(actualValue))
            {
               System.out.println("\nwe have a match!");
               points += (userLevel * 5);
               score.setText("Score: " + points);
               updateLevel(userLevel);
            }
            else
            {
               System.out.println("GAME OVER");
               gameOver = true;
            }
            System.out.println("Clearing input");
            input.clear();
            ik.removeSensorChangeListener(sensor_listener);
            ik.addSensorChangeListener(sensor_listener);
            play();
         }
         
      }
      catch(Exception ex)
      {
         System.out.println("System failure");
      }
   }
   
   
   public void play()
   {     
      if(gameOver == false && userLevel < 5)
      { 
         try{
            screen.setDisplayString(0, " ");
            resetLights();
            resetPanels();
            Thread.sleep(500);
            userLevel++;
            System.out.println("In the play method, level: " + userLevel);
            levelNum.setText("Level: " + userLevel);
            for(int f = 0; f < userLevel; f++)
            {
               if(numColor.get(f) == 0)
               {
                  System.out.println("Red");
                  Thread.sleep(400);
                  redPanel.setBackground(Color.RED);
                  ik.setOutputState(3, true);
                  Thread.sleep(750);
                  resetPanels();
                  resetLights();
               }
               if(numColor.get(f) == 1)
               {
                  System.out.println("Blue");
                  Thread.sleep(400);
                  bluePanel.setBackground(Color.BLUE);
                  ik.setOutputState(2, true);
                  Thread.sleep(750);
                  resetPanels();
                  resetLights();
               }
               if(numColor.get(f) == 2)
               {
                  System.out.println("Green");
                  Thread.sleep(400);
                  greenPanel.setBackground(Color.GREEN);
                  ik.setOutputState(1, true);
                  Thread.sleep(750);
                  resetPanels();
                  resetLights();
               }
               if(numColor.get(f) == 3)
               {
                  System.out.println("Yellow");
                  Thread.sleep(400);
                  yellowPanel.setBackground(Color.YELLOW);
                  ik.setOutputState(0, true);
                  Thread.sleep(750);
                  resetPanels();
                  resetLights();
               }
               resetPanels();
            }
         }
         
         catch(Exception ie)
         {
            System.out.println("System failure");
         }     
      }
      else
      {
         endGame();
      }
   }
   
   public void endGame()
   {
      try{
         File file = new File("cheering.wav");
         URI uri = file.toURI();
         URL url = uri.toURL();
         
         cheering = Applet.newAudioClip(url);
         
         file = new File("lost.wav");
         uri = file.toURI();
         url = uri.toURL();
         
         lost = Applet.newAudioClip(url); 
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      try{        
         screen.setDisplayString(0, " GAME OVER! Pts:" + points);
      }
      catch(Exception exc)
      {
         System.out.println("System failure");
      }
      gameOver = true;
      timer.stop();
      if(userLevel == 5)
      {
         if(min < 1 && sec <= 30)
         {
            points += 100;
            score.setText("Score: " + points);
            min1.doClick();
         }
         else if(min < 1 && sec > 30)
         {
            points += 75;
            score.setText("Score: " + points);
            min2.doClick();
         }       
         else if(min == 1 && sec <= 30)
         {
            points += 50;
            score.setText("Score: " + points);
            min3.doClick();
         }
         else if(min == 1 && sec > 30)
         {
            points += 25;
            score.setText("Score: " + points);
            min4.doClick();
         }
         else if(min == 2 && sec <= 30)
         {
            points += 10;
            score.setText("Score: " + points);
            min5.doClick();
         }
         ImageIcon imageWin = new ImageIcon("winner.jpg");
         cheering.play();
         JOptionPane.showMessageDialog(null, " ", " ", JOptionPane.INFORMATION_MESSAGE, imageWin);
      }
      else
      {
         ImageIcon imageLose = new ImageIcon("gameover.jpg");
         lost.play();
         JOptionPane.showMessageDialog(null, " ", " ", JOptionPane.INFORMATION_MESSAGE, imageLose);
      }
      try{         
         Thread.sleep(4000);
         screen.setDisplayString(0, " ");
      }
      catch(Exception excep)
      {
         System.out.println("System failure");
      }
   }
   
   public void generatePattern()
   {
      for(int i = 1; i <= 5; i++)
      {
         System.out.println("\nLevel: " + i);
         numColor.add(rand.nextInt(4));
         for(int f = 0; f < i; f++)
         {
            if(numColor.get(f) == 0)
               System.out.println("Red");
            if(numColor.get(f) == 1)
               System.out.println("Blue");
            if(numColor.get(f) == 2)
               System.out.println("Green");
            if(numColor.get(f) == 3)
               System.out.println("Yellow");
         }
      }
   }

   public void updateLevel(int l)
   {
      if(l == 1)
         lvl1.doClick();
      if(l == 2)
      {
         lvl2.doClick();
      }
      if(l == 3)
      {
         lvl3.doClick();
      }
      if(l == 4)
      {
         lvl4.doClick();
      }
      if(l == 5)
      {
         lvl5.doClick();
      }
   }
   
   public void openPhidgets()
   {
      try{
         screen = new TextLCDPhidget();
         screen.openAny();
         System.out.println("Waiting for attachment....");
         screen.waitForAttachment();
         System.out.println("LCD Screen attached: " + screen.getSerialNumber());
         
         ik = new InterfaceKitPhidget();
         ik.openAny();
         System.out.println("Waiting for interface attachments....");
         ik.waitForAttachment();
         System.out.println("Interface Kit attached: " + ik.getDeviceName());
         Phidget.enableLogging(Phidget.PHIDGET_LOG_DEBUG, null); 
         
         screen.setDisplayString(0, "Lets play Simon Says");
         ik.setOutputState(0, false);
         ik.setOutputState(1, false);
         ik.setOutputState(2, false);
         ik.setOutputState(3, false);
      }
      catch(PhidgetException e)
      {
         System.out.println("Phidget Exception Generated");
         e.printStackTrace();
      }
   }
   
   public void buildTitlePanel()
   {
      FlowLayout layout = new FlowLayout();
      layout.setAlignment(FlowLayout.CENTER); 
      titlePanel.setLayout(layout);
      titlePanel.setLayout(new GridLayout(6,1));
      Font f = new Font("Arial", Font.BOLD, 25);
      title.setHorizontalAlignment(SwingConstants.CENTER);
      title.setFont(f);
      Font ft = new Font("Arial", Font.PLAIN, 16);
      instructions1.setFont(ft);
      instructions2.setFont(ft);
      instructions3.setFont(ft);
      instructions4.setFont(ft);
      instructions1.setHorizontalAlignment(SwingConstants.CENTER);
      instructions2.setHorizontalAlignment(SwingConstants.CENTER);
      instructions3.setHorizontalAlignment(SwingConstants.CENTER);
      instructions4.setHorizontalAlignment(SwingConstants.CENTER);
      titlePanel.add(title);
      titlePanel.add(instructions1);
      titlePanel.add(instructions2);
      titlePanel.add(instructions3);
      titlePanel.add(instructions4);
      fullPanel.add(titlePanel);
   }
   
   public void buildPointsAndTimerPanel()
   {
      Font f = new Font("Arial", Font.PLAIN, 16);
      levelTitle.setFont(f);
      lvl1.setFont(f);
      lvl2.setFont(f);
      lvl3.setFont(f);
      lvl4.setFont(f);
      lvl5.setFont(f);
      pointsPanel.add(levelTitle);
      pointsPanel.add(lvl1);
      pointsPanel.add(lvl2);
      pointsPanel.add(lvl3);
      pointsPanel.add(lvl4);
      pointsPanel.add(lvl5);
      pointsPanel.setLayout(new GridLayout(6,1));
      ptPanel.add(pointsPanel);
      
      Font ft = new Font("Arial", Font.BOLD, 50);
      levelNum.setFont(ft);
      levelNum.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      ptPanel.add(levelNum);
      
      ptPanel.add(new JLabel("             "));
      
      buildTimer();
      ptPanel.add(pnlTimer);
      
      timeBonusPanel.setLayout(new GridLayout(6,1));
      bonusTitle.setFont(f);
      min1.setFont(f);
      min2.setFont(f);
      min3.setFont(f);
      min4.setFont(f);
      min5.setFont(f);
      ButtonGroup bonus = new ButtonGroup();
      bonus.add(min1);
      bonus.add(min2);
      bonus.add(min3);
      bonus.add(min4);
      bonus.add(min5);
      timeBonusPanel.add(bonusTitle);
      timeBonusPanel.add(min1);
      timeBonusPanel.add(min2);
      timeBonusPanel.add(min3);
      timeBonusPanel.add(min4);
      timeBonusPanel.add(min5);
      ptPanel.add(timeBonusPanel);
      
      fullPanel.add(ptPanel);      
   }
   
   public void buildColorPanel()
   {
      colorPanel.setLayout(new GridLayout(2,4,10,10));
      redPanel.setPreferredSize(new Dimension(70,80));
      bluePanel.setPreferredSize(new Dimension(70,80));
      greenPanel.setPreferredSize(new Dimension(70,80));
      yellowPanel.setPreferredSize(new Dimension(70,80));
      redPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      bluePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      greenPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      yellowPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      colorPanel.add(new JLabel(""));
      colorPanel.add(redPanel);
      colorPanel.add(bluePanel);
      colorPanel.add(new JLabel(""));
      colorPanel.add(new JLabel(""));
      colorPanel.add(greenPanel);
      colorPanel.add(yellowPanel); 
      colorPanel.add(new JLabel(""));
      
      fullPanel.add(colorPanel);     
   }
   
   public void buildScorePanel()
   {
      scorePanel.add(score);
      Font f = new Font("Arial", Font.BOLD, 60);
      score.setFont(f);
      add(scorePanel, BorderLayout.SOUTH);
   }
   
   public void buildTimer()
   {
      timerListener = 
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               sec++;
               if(sec == 60)
               {
                  sec = 0;
                  time.setText((min++)+":"+sec);
               }
               if(sec < 10)
                  time.setText(min+":0"+sec);
               else
                  time.setText(min+":"+sec);
            }
         };
      timer = new Timer(1000, timerListener);
      Font f = new Font("Times New Roman", Font.BOLD, 50);
      time.setFont(f);
      Font ft = new Font("Times New Roman", Font.BOLD, 50);
      timerTitle.setFont(ft);
      pnlTimer.add(timerTitle);
      pnlTimer.add(time);
      pnlTimer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      add(pnlTimer);
   }
      
     
      
   public void resetPanels()
   {
      redPanel.setBackground(null);
      greenPanel.setBackground(null);
      bluePanel.setBackground(null);
      yellowPanel.setBackground(null);
   }
   
   public void resetLights()
   {
      try{
         ik.setOutputState(0, false);
         ik.setOutputState(1, false);
         ik.setOutputState(2, false);
         ik.setOutputState(3, false);
      }
      catch(Exception e)
      {
         System.out.println("System failure");
      }
   }
   
   public static void main(String[] args)
   {
      java.awt.EventQueue.invokeLater(
         new Runnable() {
            public void run() {
               new SimonSays().setVisible(true);
            }
         });      
   }
}