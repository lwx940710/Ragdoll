/**
 * Created by lwx on 2017-02-21.
 */


import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Object;

public class BotView extends JPanel implements Observer {
    private Model model;
    JSlider slider;
    JButton playback;
    JButton addFrame;
    JButton clearAll;

    BotView(Model model) {
        this.model = model;

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        playback = new JButton("Playback");
        if (model.getTotal_frames() == 0) {
            playback.setEnabled(false);
        }
        playback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll.setEnabled(false);
                addFrame.setEnabled(false);
                model.setTime(slider.getValue());
                model.setPlayback(true);
                model.update();
            }
        });
        this.add(playback);

        slider = new JSlider(0, model.getTotal_frames());
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(1);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (slider.getValueIsAdjusting()) {
                    System.out.println("slider value  " + slider.getValue());
                    if (model.getPlayback()) {
                        model.setPlayback(false);
                    }
                    model.setTime(slider.getValue());
                    model.setPose(model.getSaved_pose(slider.getValue()));
                    System.out.println(model.getSaved_pose(0).origins[0].getX() + "    " + model.getSaved_pose(0).origins[0].getY());
                    if (slider.getValue() == model.getTotal_frames()) {
                        playback.setEnabled(false);
                    }
                    model.update();
                }
            }
        });
        this.add(slider);

        addFrame = new JButton("Add Frame");
        addFrame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setPlayback(false);

                // do savings
                if (model.getTime() != model.getTotal_frames()) {
                    model.clearFrom(model.getTime() + 1);
                    model.setTotal_frames(model.getTime() + 1);
                } else {
                    model.setTotal_frames(model.getTotal_frames() + 1);
                }
                model.setTime(model.getTotal_frames());
                model.saveFrame();
                model.update();
            }
        });
        this.add(addFrame);

        clearAll = new JButton("Clear All");
        clearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.getPlayback()) {
                    model.setPlayback(false);
                }
                // do clearing
                model.clearAll();
                model.update();
            }
        });
        this.add(clearAll);

        model.addObserver(this);
    }


    /**
     * Update with data from the model.
     */
    @Override
    public void update(Observable o, Object obj) {
        System.out.println(slider.getValue());
        System.out.println(model.getTotal_frames());

        this.slider.setMaximum(model.getTotal_frames());

        this.slider.setValue(model.getTime());

        if (slider.getValue() == model.getTotal_frames()) {
            playback.setEnabled(false);
        } else {
            playback.setEnabled(true);
        }
    }
}
