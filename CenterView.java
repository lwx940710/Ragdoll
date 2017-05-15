import javafx.geometry.Point3DBuilder;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;

/**
 * Created by lwx on 2017-02-28.
 */
public class CenterView extends JPanel implements Observer {
    private Model model;
    int width, height;

    CenterView(Model model) {
        this.model = model;
        this.setFocusable(true);
        width = this.getWidth();
        height = this.getHeight();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                width = getWidth();
                height = getHeight();
                // do mouse pressed
                if (model.getTime() < model.getTotal_frames()) {
                    model.clearFrom(model.getTime() + 1);
                    model.setTotal_frames(model.getTime());
                }
                double x = model.getTorsoOrigin().getX();
                double y = model.getTorsoOrigin().getY();
                model.setPressed_pt(e.getX(), e.getY());
                Rectangle rect = new Rectangle((int) x, (int) y, 80, 180);
                if (rect.contains(e.getX(), e.getY())) {
                    model.setTorso_draggable(true);
                    model.setTorsoDiff(e.getX() - x, e.getY() - y);
                } else {
                    model.setTorso_draggable(false);
                    model.setTorsoDiff(0, 0);
                    Ellipse2D head = new Ellipse2D.Double(model.getHeadOrigin().getX(), model.getHeadOrigin().getY(), 60, 90);

                    Ellipse2D l_u_arm = new Ellipse2D.Double(model.getLeftArmOrigin(0).getX(), model.getLeftArmOrigin(0).getY(), 25, 100);
                    Ellipse2D l_l_arm = new Ellipse2D.Double(model.getLeftArmOrigin(1).getX(), model.getLeftArmOrigin(1).getY(), 20, 80);
                    Ellipse2D l_hand = new Ellipse2D.Double(model.getLeftArmOrigin(2).getX(), model.getLeftArmOrigin(2).getY(), 25, 30);

                    Ellipse2D r_u_arm = new Ellipse2D.Double(model.getRightArmOrigin(0).getX(), model.getRightArmOrigin(0).getY(), 25, 100);
                    Ellipse2D r_l_arm = new Ellipse2D.Double(model.getRightArmOrigin(1).getX(), model.getRightArmOrigin(1).getY(), 20, 80);
                    Ellipse2D r_hand = new Ellipse2D.Double(model.getRightArmOrigin(2).getX(), model.getRightArmOrigin(2).getY(), 25, 30);

                    Ellipse2D l_u_leg = new Ellipse2D.Double(model.getLeftLegOrigin(0).getX(), model.getLeftLegOrigin(0).getY(), 25, 140 * model.getLeft_upper_ratio());
                    Ellipse2D l_l_leg = new Ellipse2D.Double(model.getLeftLegOrigin(1).getX(), model.getLeftLegOrigin(1).getY(), 20, 110 * model.getLeft_lower_ratio());
                    Ellipse2D l_foot = new Ellipse2D.Double(model.getLeftLegOrigin(2).getX(), model.getLeftLegOrigin(2).getY(), 45, 20);

                    Ellipse2D r_u_leg = new Ellipse2D.Double(model.getRightLegOrigin(0).getX(), model.getRightLegOrigin(0).getY(), 25, 140 * model.getRight_upper_ratio());
                    Ellipse2D r_l_leg = new Ellipse2D.Double(model.getRightLegOrigin(1).getX(), model.getRightLegOrigin(1).getY(), 20, 110 * model.getRight_lower_ratio());
                    Ellipse2D r_foot = new Ellipse2D.Double(model.getRightLegOrigin(2).getX(), model.getRightLegOrigin(2).getY(), 45, 20);

                    Point mouse = new Point();
                    mouse.setLocation(e.getX(), e.getY());
                    //System.out.println("mouse x = " + mouse.getX() + " y = " + mouse.getY());
                    Point2D p = null;
                    try {
                        // head
                        p = model.getHead_transform().inverseTransform(mouse, model.getSrcs(0));
                        if (head.contains(p.getX(), p.getY())) {
                            model.setHead_draggable(true);
                            //System.out.println("head");
                        }

                        // left arm
                        p = model.getLeft_arm_transform(0).inverseTransform(mouse, model.getSrcs(2));
                        if (l_u_arm.contains(p.getX(), p.getY())) {
                            model.setL_u_arm_draggable(true);
                            //System.out.println("left upper arm");
                        }

                        p = model.getLeft_arm_transform(1).inverseTransform(mouse, model.getSrcs(3));
                        if (l_l_arm.contains(p.getX(), p.getY())) {
                            model.setL_l_arm_draggable(true);
                            //System.out.println("left lower arm");
                        }

                        p = model.getLeft_arm_transform(2).inverseTransform(mouse, model.getSrcs(4));
                        if (l_hand.contains(p.getX(), p.getY())) {
                            model.setL_hand_draggable(true);
                            //System.out.println("left hand");
                        }

                        // right arm
                        p = model.getRight_arm_transform(0).inverseTransform(mouse, model.getSrcs(5));
                        if (r_u_arm.contains(p.getX(), p.getY())) {
                            model.setR_u_arm_draggable(true);
                            //System.out.println("right upper arm");
                        }

                        p = model.getRight_arm_transform(1).inverseTransform(mouse, model.getSrcs(6));
                        if (r_l_arm.contains(p.getX(), p.getY())) {
                            model.setR_l_arm_draggable(true);
                            //System.out.println("right lower arm");
                        }

                        p = model.getRight_arm_transform(2).inverseTransform(mouse, model.getSrcs(7));
                        if (r_hand.contains(p.getX(), p.getY())) {
                            model.setR_hand_draggable(true);
                            //System.out.println("right hand");
                        }

                        // left leg
                        p = model.getLeft_leg_transform(0).inverseTransform(mouse, model.getSrcs(8));
                        if (l_u_leg.contains(p.getX(), p.getY())) {
                            model.setL_u_leg_draggable(true);
                            //System.out.println("left upper leg");
                        }

                        p = model.getLeft_leg_transform(1).inverseTransform(mouse, model.getSrcs(9));
                        if (l_l_leg.contains(p.getX(), p.getY())) {
                            model.setL_l_leg_draggable(true);
                            //System.out.println("left lower leg");
                        }

                        p = model.getLeft_leg_transform(2).inverseTransform(mouse, model.getSrcs(10));
                        //System.out.println("p  x = " + p.getX() + " y = " + p.getY());
                        if (l_foot.contains(p.getX(), p.getY())) {
                            model.setL_foot_draggable(true);
                            //System.out.println("left foot");
                        }

                        // right leg
                        p = model.getRight_leg_transform(0).inverseTransform(mouse, model.getSrcs(11));
                        if (r_u_leg.contains(p.getX(), p.getY())) {
                            model.setR_u_leg_draggable(true);
                            System.out.println("right upper leg");
                        }

                        p = model.getRight_leg_transform(1).inverseTransform(mouse, model.getSrcs(12));
                        if (r_l_leg.contains(p.getX(), p.getY())) {
                            model.setR_l_leg_draggable(true);
                            System.out.println("right lower leg");
                        }

                        p = model.getRight_leg_transform(2).inverseTransform(mouse, model.getSrcs(13));
                        if (r_foot.contains(p.getX(), p.getY())) {
                            model.setR_foot_draggable(true);
                            //System.out.println("right foot");
                        }

                    } catch (NoninvertibleTransformException nte) {
                        nte.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                // do mouse released
                model.setHead_draggable(false);
                model.setTorso_draggable(false);
                model.setL_u_arm_draggable(false);
                model.setL_l_arm_draggable(false);
                model.setL_hand_draggable(false);
                model.setR_u_arm_draggable(false);
                model.setR_l_arm_draggable(false);
                model.setR_hand_draggable(false);
                model.setL_u_leg_draggable(false);
                model.setL_l_leg_draggable(false);
                model.setL_foot_draggable(false);
                model.setR_u_leg_draggable(false);
                model.setR_l_leg_draggable(false);
                model.setR_foot_draggable(false);
                model.setTorsoDiff(0, 0);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                // do mouse dragged
                if (model.getTorso_draggable()) {
                    model.setTorsoOrigin(e.getX(), e.getY());
                } else if (model.getPressed_pt().getX() != e.getX()) {
                    double tanr = (e.getY() - model.getPressed_pt().getY()) / (e.getX() - model.getPressed_pt().getX()) / 2;
                    double r = Math.atan(tanr);
                    if (model.getHead_draggable()) {
                        if (r > 0.872) {
                            r = 0.872;
                        }
                        if (r < -0.872) {
                            r = -0.872;
                        }
                        model.setRadHead(r);
                    } else if (model.getL_u_arm_draggable()) {
                        model.setRadLeftArm(0, r);
                    } else if (model.getL_l_arm_draggable()) {
                        if (r > 2.356) {
                            r = 2.356;
                        }
                        if (r < -2.356) {
                            r = -2.356;
                        }
                        model.setRadLeftArm(1, r);
                    } else if (model.getL_hand_draggable()) {
                        if (r > 0.611) {
                            r = 0.611;
                        }
                        if (r < -0.611) {
                            r = -0.611;
                        }
                        model.setRadLeftArm(2, r);
                    } else if (model.getR_u_arm_draggable()) {
                        model.setRadRightArm(0, r);
                    } else if (model.getR_l_arm_draggable()) {
                        if (r > 2.356) {
                            r = 2.356;
                        }
                        if (r < -2.356) {
                            r = -2.356;
                        }
                        model.setRadRightArm(1, r);
                    } else if (model.getR_hand_draggable()) {
                        if (r > 0.611) {
                            r = 0.611;
                        }
                        if (r < -0.611) {
                            r = -0.611;
                        }
                        model.setRadRightArm(2, r);
                    } else if (model.getL_u_leg_draggable()) {
                        if (r > 1.571) {
                            r = 1.571;
                        }
                        if (r < -1.571) {
                            r = -1.571;
                        }
                        model.setRadLeftLeg(0, r);
                    } else if (model.getL_l_leg_draggable()) {
                        if (r > 1.571) {
                            r = 1.571;
                        }
                        if (r < -1.571) {
                            r = -1.571;
                        }
                        model.setRadLeftLeg(1, r);
                    } else if (model.getL_foot_draggable()) {
                        if (r > 0.611) {
                            r = 0.611;
                        }
                        if (r < -0.611) {
                            r = -0.611;
                        }
                        model.setRadLeftLeg(2, r);
                    } else if (model.getR_u_leg_draggable()) {
                        if (r > 1.571) {
                            r = 1.571;
                        }
                        if (r < -1.571) {
                            r = -1.571;
                        }
                        model.setRadRightLeg(0, r);
                    } else if (model.getR_l_leg_draggable()) {
                        if (r > 1.571) {
                            r = 1.571;
                        }
                        if (r < -1.571) {
                            r = -1.571;
                        }
                        model.setRadRightLeg(1, r);
                    } else if (model.getR_foot_draggable()) {
                        if (r > 0.611) {
                            r = 0.611;
                        }
                        if (r < -0.611) {
                            r = -0.611;
                        }
                        model.setRadRightLeg(2, r);
                    }
                }

                if (model.getL_u_leg_draggable()) {
                    double x_origin = model.getSrcs(8).getX();
                    double y_origin = model.getSrcs(8).getY();
                    double temp1 = e.getX() - x_origin;
                    temp1 = temp1 * temp1;
                    double temp2 = e.getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l1 = Math.sqrt(temp1 + temp2);

                    temp1 = model.getPressed_pt().getX() - x_origin;
                    temp1 = temp1 * temp1;
                    temp2 = model.getPressed_pt().getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l2 = Math.sqrt(temp1 + temp2);

                    model.setLeft_upper_ratio(l1 / l2 / 3);
                    model.setLeft_lower_ratio(l1 / l2 / 3);
                }

                if (model.getL_l_leg_draggable()) {
                    double x_origin = model.getSrcs(9).getX();
                    double y_origin = model.getSrcs(9).getY();
                    double temp1 = e.getX() - x_origin;
                    temp1 = temp1 * temp1;
                    double temp2 = e.getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l1 = Math.sqrt(temp1 + temp2);

                    temp1 = model.getPressed_pt().getX() - x_origin;
                    temp1 = temp1 * temp1;
                    temp2 = model.getPressed_pt().getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l2 = Math.sqrt(temp1 + temp2);

                    model.setLeft_lower_ratio(l1 / l2 / 3);
                }

                if (model.getR_u_leg_draggable()) {
                    double x_origin = model.getSrcs(11).getX();
                    double y_origin = model.getSrcs(11).getY();
                    double temp1 = e.getX() - x_origin;
                    temp1 = temp1 * temp1;
                    double temp2 = e.getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l1 = Math.sqrt(temp1 + temp2);

                    temp1 = model.getPressed_pt().getX() - x_origin;
                    temp1 = temp1 * temp1;
                    temp2 = model.getPressed_pt().getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l2 = Math.sqrt(temp1 + temp2);

                    model.setRight_upper_ratio(l1 / l2 / 3);
                    model.setRight_lower_ratio(l1 / l2 / 3);
                }

                if (model.getR_l_leg_draggable()) {
                    double x_origin = model.getSrcs(12).getX();
                    double y_origin = model.getSrcs(12).getY();
                    double temp1 = e.getX() - x_origin;
                    temp1 = temp1 * temp1;
                    double temp2 = e.getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l1 = Math.sqrt(temp1 + temp2);

                    temp1 = model.getPressed_pt().getX() - x_origin;
                    temp1 = temp1 * temp1;
                    temp2 = model.getPressed_pt().getY() - y_origin;
                    temp2 = temp2 * temp2;
                    double l2 = Math.sqrt(temp1 + temp2);

                    model.setRight_lower_ratio(l1 / l2 / 3);
                }

                repaint();
            }
        });

        model.addObserver(this);
    }

    public void drawTorso(Graphics2D g2d, Boolean just_new, double x, double y) {

        File texture_file = new File("texture.png");
        BufferedImage img = null;
        try {
            img = ImageIO.read(texture_file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Rectangle rect = null;
        double r = 0;
        g2d.setTransform(AffineTransform.getRotateInstance(r, x, y));
        rect = new Rectangle((int) x, (int) y, 80, 180);
        model.setSrcs(x, y, 1);

        g2d.setClip(rect);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(rect);
        drawHead(g2d, just_new, img, x, y);
        drawR_U_Arm(g2d, just_new, img, x, y);
        drawL_U_Arm(g2d, just_new, img, x, y);
        drawR_U_Leg(g2d, just_new, img, x, y);
        drawL_U_Leg(g2d, just_new, img, x, y);
    }

    public void drawHead(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        double r = model.getRadHead();
        x = x + 40;
        AffineTransform a = new AffineTransform();
        a.rotate(r, x, y);
        model.setSrcs(x, y, 0);
        model.setHead_transform(a);
        g2d.setTransform(a);

        x = x - 30;
        y = y - 90;

        Ellipse2D e = new Ellipse2D.Double(x, y, 60, 90);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);

        model.setHeadOrigin(x, y);
    }

    public void drawR_U_Arm(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        double r = model.getRadRightArm(0);

        x = x + 80 - 12.5; // 80
        if (r > 0.052 && r < 1.57) {
            x = x + 12.5;
        }
        if (r < -1) {
            //x = x - 12.5;
            y = y + 5;
        }
        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_arm_transform(a, 0);
        g2d.setTransform(a);
        model.setSrcs(x, y, 5);

        if (r > 0.052 && r < 1.57) {
            x = x - 12.5;
        }
        if (r < -1) {
            y = y - 5;
        }
        y = y + 10;
        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 100);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        y = y - 10;
        model.setRightArmOrigin(x, y, 0);
        drawR_L_Arm(g2d, just_new, img, x, y);
    }

    public void drawR_L_Arm(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();
        if (model.getRadRightArm(0) != 0) {
            p.setLocation(x - 100 * Math.sin(model.getRadRightArm(0)),
                    y + 100 * Math.cos(model.getRadRightArm(0)));
        } else {
            p.setLocation(x, y + 100);
        }

        double r = model.getRadRightArm(1);
        r = r + model.getRadRightArm(0);

        x = p.getX();
        y = p.getY();
        double diff = 0;

        if (r < 1.6 && r > 0.5) {
            diff = (r - 0.5) / 0.1;
            x += diff;
            y += diff;
        }

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_arm_transform(a, 1);
        g2d.setTransform(a);
        model.setSrcs(x, y, 6);

        x -= diff;
        y -= diff;

        Ellipse2D e = new Ellipse2D.Double(x, y, 20, 80);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);

        model.setRightArmOrigin(x, y, 1);
        drawR_Hand(g2d, just_new, img, x, y);
    }

    public void drawR_Hand(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 80 * Math.sin(model.getRadRightArm(1) + model.getRadRightArm(0)),
                y + 80 * Math.cos(model.getRadRightArm(1) + model.getRadRightArm(0)));

        double r = model.getRadRightArm(2);
        r = r + model.getRadRightArm(1);

        x = p.getX();
        y = p.getY();
        double diff = 0;

        if (r >= 0.5 && r < 0.6) {
            x += 13;
        } else if (r < 0) {
            x -= 13;
        } else {
            diff = diff + (r - 0.1) / 0.1;
            x += diff;
        }

        y -= 5;

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_arm_transform(a, 2);
        g2d.setTransform(a);
        model.setSrcs(x, y, 7);

        if (r >= 0.5 && r < 0.6) {
            x -= 13;
        } else if (r < 0) {
            x += 13;
        } else {
            x -= diff;
        }

        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 30);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setRightArmOrigin(x, y, 2);
    }

    public void drawL_U_Arm(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        double r = model.getRadLeftArm(0);
        x = x - 12.5;
        if (r > 0.53 && r < 1.57) {
            x = x + 12.5;
        }
        if (r < -1) {
            y = y + 5;
        }
        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_arm_transform(a, 0);
        g2d.setTransform(a);
        model.setSrcs(x, y, 2);
        if (r > 0.53 && r < 1.57) {
            x = x - 12.5;
        }
        if (r < -1) {
            y = y - 5;
        }
        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 100);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setLeftArmOrigin(x, y, 0);
        drawL_L_Arm(g2d, just_new, img, x, y);
    }

    public void drawL_L_Arm(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 100 * Math.sin(model.getRadLeftArm(0)),
                y + 100 * Math.cos(model.getRadLeftArm(0)));


        double r = model.getRadLeftArm(1);
        r = r + model.getRadLeftArm(0);


        x = p.getX();
        y = p.getY();

        double diff = 0;
        if (r < 2.2 && r > 0.5) {
            diff = (r - 0.5) / 0.2;
            x += diff;
            y += diff;

        }
        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_arm_transform(a, 1);
        g2d.setTransform(a);
        model.setSrcs(x, y, 3);

        x -= diff;
        y -= diff;

        Ellipse2D e = new Ellipse2D.Double(x, y, 20, 80);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setLeftArmOrigin(x, y, 1);
        drawL_Hand(g2d, just_new, img, x, y);
    }

    public void drawL_Hand(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();


        p.setLocation(x - 80 * Math.sin(model.getRadLeftArm(1) + model.getRadLeftArm(0)),
                y + 80 * Math.cos(model.getRadLeftArm(1) + model.getRadLeftArm(0)));

        double r = model.getRadLeftArm(2);
        x = p.getX();
        y = p.getY();

        double diff = 5;
        if (r >= 0.5 && r < 0.6) {
            x += 13;
        } else if (r < 0) {
            x -= 13;
        } else {
            diff = diff + (r - 0.1) / 0.1;
            x += diff;
        }

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_arm_transform(a, 2);
        g2d.setTransform(a);
        model.setSrcs(x, y, 4);

        if (r >= 0.5 && r < 0.6) {
            x -= 13;
        } else if (r < 0) {
            x += 13;
        } else {
            x -= diff;
        }

        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 30);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);

        model.setLeftArmOrigin(x, y, 2);
    }

    public void drawL_U_Leg(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        double r = model.getRadLeftLeg(0);

        y = y + 180;

        double diff = (r - 0.2) / 0.07;
        y -= diff;

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_leg_transform(a, 0);
        g2d.setTransform(a);
        model.setSrcs(x, y, 8);

        y += diff;

        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 140 * model.getLeft_upper_ratio());

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);

        model.setLeftLegOrigin(x, y, 0);
        drawL_L_Leg(g2d, just_new, img, x, y);
    }

    public void drawL_L_Leg(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 140 * model.getLeft_upper_ratio() * Math.sin(model.getRadLeftLeg(0)),
                y + 140 * model.getLeft_upper_ratio() * Math.cos(model.getRadLeftLeg(0)));

        double r = model.getRadLeftLeg(1);
        r = r + model.getRadLeftLeg(0);
        x = p.getX();
        y = p.getY();

        double diff = 0;
        if (r > 0 && r < 1.6) {
            diff = (r - 0.1) / 0.1;
            x += diff;
        }
        if (r < 0 && r > -1.6) {
            diff = (r - 0.1) / 0.1;
            x -= diff;
        }

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_leg_transform(a, 1);
        g2d.setTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setSrcs(x, y, 9);

        if (r > 0 && r < 1.6) {
            x -= diff;
        }
        if (r < 0 && r > -1.6) {
            x += diff;
        }

        double diff2 = model.getRadLeftLeg(0);
        if (diff2 < 0) {
            if (diff2 < -0.7) {
                diff2 = 8 + Math.abs(diff2) / 0.1;
            } else {
                diff2 = Math.abs(diff2) / 0.1;
            }
        }

        x -= diff2;

        Ellipse2D e = new Ellipse2D.Double(x, y, 20, 110 * model.getLeft_lower_ratio());

        x += diff2;

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setLeftLegOrigin(x, y, 1);
        drawL_Foot(g2d, just_new, img, x, y);
    }

    public void drawL_Foot(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 110 * model.getLeft_lower_ratio() * Math.sin(model.getRadLeftLeg(1) + model.getRadLeftLeg(0)),
                y + 110 * model.getLeft_lower_ratio() * Math.cos(model.getRadLeftLeg(1) + model.getRadLeftLeg(0)));

        double r = model.getRadLeftLeg(2);
        x = p.getX();
        y = p.getY();

        x += 10;

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setLeft_leg_transform(a, 2);
        g2d.setTransform(a);
        model.setSrcs(x, y, 10);

        x = x - 25;
        x -= 10;

        double diff2 = model.getRadLeftLeg(0);
        if (diff2 < 0) {
            if (diff2 < -0.7) {
                diff2 = 8 + Math.abs(diff2) / 0.1;
            } else {
                diff2 = Math.abs(diff2) / 0.1;
            }
        }

        x -= diff2;

        Ellipse2D e = new Ellipse2D.Double(x, y, 45, 20);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setLeftLegOrigin(x, y, 2);
    }

    public void drawR_U_Leg(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {
        double r = model.getRadRightLeg(0);

        x = x + 55;
        y = y + 180;

        double diff = (r - 0.2) / 0.07;
        y -= diff;

        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_leg_transform(a, 0);
        g2d.setTransform(a);
        model.setSrcs(x, y, 11);

        y += diff;
        Ellipse2D e = new Ellipse2D.Double(x, y, 25, 140 * model.getRight_upper_ratio());

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setRightLegOrigin(x, y, 0);
        drawR_L_Leg(g2d, just_new, img, x, y);
    }

    public void drawR_L_Leg(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 140 * model.getRight_upper_ratio() * Math.sin(model.getRadRightLeg(0)),
                y + 140 * model.getRight_upper_ratio() * Math.cos(model.getRadRightLeg(0)));

        double r = model.getRadRightLeg(1);
        r = r + model.getRadRightLeg(0);

        x = p.getX();
        y = p.getY();

        double diff = 0;

        if (r > 0 && r < 1.6) {
            diff = (r - 0.1) / 0.1;
            x += diff;
        }
        if (r < 0 && r > -1.6) {
            diff = (r - 0.1) / 0.1;
            x -= diff;
        }
        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_leg_transform(a, 1);
        g2d.setTransform(a);
        model.setSrcs(x, y, 12);


        if (r > 0 && r < 1.6) {
            x -= diff;
        }
        if (r < 0 && r > -1.6) {
            x += diff;
        }


        double diff2 = model.getRadRightLeg(0);
        if (diff2 < 0) {
            if (diff2 < -0.7) {
                diff2 = 8 + Math.abs(diff2) / 0.1;
            } else {
                diff2 = Math.abs(diff2) / 0.1;
            }
        }
        x -= diff2;

        Ellipse2D e = new Ellipse2D.Double(x, y, 20, 110 * model.getRight_lower_ratio());

        x += diff2;


        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setRightLegOrigin(x, y, 1);
        drawR_Foot(g2d, just_new, img, x, y);
    }

    public void drawR_Foot(Graphics2D g2d, Boolean just_new, BufferedImage img, double x, double y) {

        Point p = new Point();

        p.setLocation(x - 110 * model.getRight_lower_ratio() * Math.sin(model.getRadRightLeg(1) + model.getRadRightLeg(0)),
                y + 110 * model.getRight_lower_ratio() * Math.cos(model.getRadRightLeg(1) + model.getRadRightLeg(0)));

        double r = model.getRadRightLeg(2);

        x = p.getX();
        y = p.getY();

        double diff = 0;

        x -= 10;
        AffineTransform a = new AffineTransform(AffineTransform.getRotateInstance(r, x, y));
        model.setRight_leg_transform(a, 2);
        g2d.setTransform(a);
        model.setSrcs(x, y, 13);

        x += 10;

        double diff2 = model.getRadRightLeg(0);
        if (diff2 < 0) {
            if (diff2 < -0.7) {
                diff2 = 8 + Math.abs(diff2) / 0.1;
            } else {
                diff2 = Math.abs(diff2) / 0.1;
            }
        }
        x -= diff2;

        Ellipse2D e = new Ellipse2D.Double(x, y, 45, 20);

        g2d.setClip(e);
        g2d.drawImage(img, 0, 0, this);
        g2d.setClip(null);
        g2d.draw(e);
        model.setRightLegOrigin(x, y, 2);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // torse
        if (model.getPlayback()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                ;
            }
            if (model.getTime() <= model.getTotal_frames()) {
                Model.Pose p = model.getSaved_pose(model.getTime());
                model.setPose(p);

                drawTorso(g2d, false, model.getTorsoOrigin().getX(), model.getTorsoOrigin().getY());
                if (model.getTime() == model.getTotal_frames()) {
                    model.setPlayback(false);
                    model.update();
                    return;
                }
                model.update();
                model.setTime(model.getTime() + 1);

            }

        } else {
            model.setTorsoOrigin(model.getTorsoOrigin().getX() - model.getTorsoDiff().getX(),
                    model.getTorsoOrigin().getY() - model.getTorsoDiff().getY());
            drawTorso(g2d, model.getJust_new(), model.getTorsoOrigin().getX(), model.getTorsoOrigin().getY());
        }


    }


    /**
     * Update with data from the model.
     */
    public void update(Observable o, Object obj) {

        repaint();
    }
}