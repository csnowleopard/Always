//   Copyright 2011 Palantir Technologies
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
// 
//       http://www.apache.org/licenses/LICENSE-2.0
// 
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.palantir.ptoss.cinch.example;

import java.util.Calendar;
import java.util.Date;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.common.base.Strings;
import com.palantir.ptoss.cinch.core.Bindable;
import com.palantir.ptoss.cinch.core.Bindings;
import com.palantir.ptoss.cinch.core.DefaultBindableModel;
import com.palantir.ptoss.cinch.swing.Action;
import com.palantir.ptoss.cinch.swing.Always;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.EnabledIf;
import com.palantir.ptoss.cinch.swing.OnChange;

public class IntroCinchMVC {

    public static class IntroModel extends DefaultBindableModel {
    	private String currentColor = "";
    	private ImageIcon currentRealColor = null;
        private String savedColor = "";
        private String body = "";
        private int red;
        private int blue;
        private int green;
        private Date myCal = null;

        public String getBody() {
            return myCal.toString();
        }

        public void setBody(String body) {
            this.body = body;
            update();
        }

        public String savedColor() {
            return savedColor;
        }

        public void setSavedColor(String savedColor) {
            this.savedColor = savedColor;
            update();
        }

        @SuppressWarnings("deprecation")
		public String getcurrentColor() {
        	red = (int) (255*((myCal.getHours()-1)/(11.0)));
        	green = (int) (255*((myCal.getMinutes())/59.0));
        	blue = (int) (255*((myCal.getSeconds())/59.0));
        	currentColor = red+" "+green+" "+blue;
            return currentColor;
        }

        public void setCurrentColor(String currentColor) {
            this.currentColor = currentColor;
            update();
        }

        public Icon getCurrentMessage() {
        	myCal = Calendar.getInstance().getTime();
        	ImageIcon img = new ImageIcon();
        	BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        	Graphics2D g = bi.createGraphics();
        	Color c = new Color(red,blue, green);
        	
        	g.setColor(c);
        	g.fill(new Rectangle(0,0,bi.getHeight(), bi.getWidth()));
        	img.setImage(bi);
        	//g.dispose();
        	currentRealColor = img;
        	return img;
        	
           /* if (Strings.isNullOrEmpty(to)) {
                return "Fill out 'To' field.";
            }
            if (Strings.isNullOrEmpty(subject)) {
                return "Fill out 'Subject' field.";
            }
            if (Strings.isNullOrEmpty(body)) {
                return "Fill out 'Body'.";
            }
            return "Ready to send.";*/
        }



        @Override
        public String toString() {
            return "IntroModel [currentColor=" + currentColor + ", savedColor=" + savedColor + ", colors=" + "colorful" + "]";
        }
    }

    public static class IntroController {
        private IntroModel model;

        public IntroController(final IntroModel model) {
            this.model = model;
        }

        public void sendEmail() {
            System.out.println("Send: " + model);
        }

        public void yell() {
            model.setBody(model.getBody().toUpperCase());
        }
        
        public void updateTime() {
        	
            model.setBody(model.getBody());
            System.out.println(model.getBody());
        }
        
        public ImageIcon getColorIcon()
        {
        	return model.currentRealColor;
        }
        
    }

    //@Always(call = "updateTime")
    private final JPanel panel = new JPanel();

    private final Bindings bindings = new Bindings();

    private final IntroModel model = new IntroModel();

    @SuppressWarnings("unused")
    @Bindable
    private final IntroController controller = new IntroController(model);

    @Bound(to = "currentColor")
    private final JTextField toField = new JTextField();

    @Bound(to = "savedColor")
    private final JTextField subjectField = new JTextField();

    @Bound(to = "body")
    private final JTextField bodyArea = new JTextField();

    //@Action(call = "yell")
    @Always(call = "updateTime")
    private final JButton yellButton = new JButton("YELL!");

    //@Action(call = "sendEmail")
    //@EnabledIf(to = "ready")
    //private final JButton sendButton = new JButton("Send");


    @Bound(to = "currentMessage")
    private final JLabel messageLabel = new JLabel((Icon)controller.getColorIcon());

    public IntroCinchMVC() {
        initializeInterface();
        bindings.bind(this);
    }

    private void initializeInterface() {
        JPanel toPanel = new JPanel(new BorderLayout());
        toPanel.add(new JLabel("To"), BorderLayout.NORTH);
        toPanel.add(toField, BorderLayout.CENTER);
        toPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.add(new JLabel("Subject"), BorderLayout.NORTH);
        subjectPanel.add(subjectField, BorderLayout.CENTER);
        subjectPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

       // JPanel bodyPanel = new JPanel(new BorderLayout());
       
        //bodyPanel.add(new JScrollPane(bodyArea), BorderLayout.CENTER);
        //bodyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toPanel, BorderLayout.NORTH);
        topPanel.add(subjectPanel, BorderLayout.SOUTH);
       // topPanel.add(new JLabel("Body"), BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        //buttonPanel.add(yellButton);
        //buttonPanel.add(sendButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        bodyArea.setPreferredSize(new Dimension(400, 400));

        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
       // panel.add(bodyPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
    }

    public JComponent getDisplayComponent() {

        return panel;
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                IntroCinchMVC example = new IntroCinchMVC();
                JFrame frame = Examples.getFrameFor("Cinch Email Example", example.panel);
                System.out.println("crux");
                frame.pack();
                frame.setVisible(true);
                
            }
        });
    }
}
