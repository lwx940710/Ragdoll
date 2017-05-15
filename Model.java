
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class Model extends Observable {
    /**
     * The observers that are watching this model for changes.
     */
    private ArrayList<Observer> observers;

    public class Pose {
        public Point[] origins;
        public double[] rads;
        public AffineTransform[] transforms;
        public Point[] srcs;
        public double[] ratios;

        Pose(Point[] o, double[] r, AffineTransform[] t, Point[] s, double[] ra) {
            origins = o;
            rads = r;
            transforms = t;
            srcs = s;
            ratios = ra;
        }
    }

    private JFrame frame;
    private Boolean just_new;
    private Point torsoOrigin, headOrigin;
    private Point[] leftArmOrigin, rightArmOrigin;
    private Point[] leftLegOrigin, rightLegOrigin;
    private Point torsoDiff;
    private double radHead;
    private double[] radRightArm;
    private double[] radLeftArm;
    private double[] radRightLeg;
    private double[] radLeftLeg;
    private boolean torso_draggable, head_draggable;
    private boolean l_u_arm_draggable, l_l_arm_draggable, l_hand_draggable;
    private boolean r_u_arm_draggable, r_l_arm_draggable, r_hand_draggable;
    private boolean l_u_leg_draggable, l_l_leg_draggable, l_foot_draggable;
    private boolean r_u_leg_draggable, r_l_leg_draggable, r_foot_draggable;
    private AffineTransform head_transform;
    private AffineTransform[] left_arm_transform, right_arm_transform;
    private AffineTransform[] left_leg_transform, right_leg_transform;
    private Point[] srcs;
    private Point pressed_pt;
    private double left_upper_ratio, left_lower_ratio, right_upper_ratio, right_lower_ratio;
    private int total_frames;
    private boolean playback;
    private int time;
    private ArrayList<Pose> saved_poses;

    public Model(JFrame frame) {
        this.observers = new ArrayList();
        this.frame = frame;
        just_new = true;
        torso_draggable = false;
        head_draggable = false;
        l_u_arm_draggable = false;
        l_l_arm_draggable = false;
        l_hand_draggable = false;
        r_u_arm_draggable = false;
        r_l_arm_draggable = false;
        r_hand_draggable = false;
        l_u_leg_draggable = false;
        l_l_leg_draggable = false;
        l_foot_draggable = false;
        r_u_leg_draggable = false;
        r_l_leg_draggable = false;
        r_foot_draggable = false;
        torsoOrigin = new Point(360, 200);
        torsoDiff = new Point(0, 0);
        headOrigin = new Point();
        leftArmOrigin = new Point[]{new Point(), new Point(), new Point()};
        rightArmOrigin = new Point[]{new Point(), new Point(), new Point()};
        leftLegOrigin = new Point[]{new Point(), new Point(), new Point()};
        rightLegOrigin = new Point[]{new Point(), new Point(), new Point()};

        radHead = 0;
        radLeftArm = new double[]{(double) 30 / (double) 180 * 3.14, 0, 0};
        radLeftLeg = new double[]{0, 0, 0};

        radRightArm = new double[]{(double) 330 / (double) 180 * 3.14, 0, 0};
        radRightLeg = new double[]{0, 0, 0};

        head_transform = new AffineTransform();
        left_arm_transform = new AffineTransform[]{null, null, null};
        right_arm_transform = new AffineTransform[]{null, null, null};
        left_leg_transform = new AffineTransform[]{null, null, null};
        right_leg_transform = new AffineTransform[]{null, null, null};
        // head - 0      torso - 1
        // l_u_arm - 2   l_l_arm - 3   l_hand - 4
        // r_u_arm - 5   r_l_arm - 6   r_hand - 7
        // l_u_leg 8     l_l_leg 9     l_foot 10
        // r_u_leg 11    r_l_leg 12    r_foot 13
        srcs = new Point[]{new Point(), new Point(), new Point(), new Point(),
                new Point(), new Point(), new Point(), new Point(),
                new Point(), new Point(), new Point(), new Point(),
                new Point(), new Point()}; // 14

        pressed_pt = new Point();

        left_upper_ratio = 1;
        left_lower_ratio = 1;
        right_upper_ratio = 1;
        right_lower_ratio = 1;

        total_frames = 0;

        saved_poses = new ArrayList<Pose>();


        // private Point torsoOrigin, headOrigin;
        // private Point[] leftArmOrigin, rightArmOrigin;
        // private Point[] leftLegOrigin, rightLegOrigin;
        Point[] origins = new Point[]{new Point(360, 200), headOrigin,
                leftArmOrigin[0], leftArmOrigin[1], leftArmOrigin[2],
                rightArmOrigin[0], rightArmOrigin[1], rightArmOrigin[2],
                leftLegOrigin[0], leftLegOrigin[1], leftLegOrigin[2],
                rightLegOrigin[0], rightLegOrigin[1], rightLegOrigin[2]};

        // private double radHead;
        // private double[] radRightArm;
        // private double[] radLeftArm;
        // private double[] radRightLeg;
        // private double[] radLeftLeg;
        double[] rads = new double[]{radHead,
                radLeftArm[0], radLeftArm[1], radLeftArm[2],
                radRightArm[0], radRightArm[1], radRightArm[2],
                radLeftLeg[0], radLeftLeg[1], radLeftLeg[2],
                radRightLeg[0], radRightLeg[1], radRightLeg[2]};

        // private AffineTransform head_transform;
        // private AffineTransform[] left_arm_transform, right_arm_transform;
        // private AffineTransform[] left_leg_transform, right_leg_transform;
        AffineTransform[] transforms = new AffineTransform[]{head_transform,
                left_arm_transform[0], left_arm_transform[1], left_arm_transform[2],
                right_arm_transform[0], right_arm_transform[1], right_arm_transform[2],
                left_leg_transform[0], left_leg_transform[1], left_leg_transform[2],
                right_leg_transform[0], right_leg_transform[1], right_leg_transform[2]};

        Point[] src_pts = srcs;
        double[] ratios = new double[]{left_upper_ratio, left_lower_ratio, right_upper_ratio, right_lower_ratio};

        Pose p = new Pose(origins, rads, transforms, src_pts, ratios);
        saved_poses.add(0, p);

        setChanged();
    }

    public JFrame getFrame() {
        return frame;
    }

    public Boolean save() {
        JFileChooser chooser = new JFileChooser();
        File file = null;
        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            String file_name = file.getName();
            if (!file_name.endsWith(".pdtxt")) {
                file = new File(file.toString() + ".pdtxt");
            }
            chooser.setCurrentDirectory(file.getParentFile());

            if (file != null) {
                file_name = file.getName();
                try {
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    // write stuff
                    // bw.write(...);
                    bw.close();
                    return true;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }

    public Boolean load() {
        JFileChooser chooser = new JFileChooser();
        File file = null;
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            chooser.setCurrentDirectory(file.getParentFile());
            String file_name = file.getName();
            if (file_name.endsWith(".pdtxt")) {
                try {
                    Scanner scanner = new Scanner(file);
                    // read stuff
                    // scanner.nextInt();
                    update();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public Boolean getJust_new() {
        return just_new;
    }

    public void setJust_new(Boolean b) {
        just_new = b;
    }

    public Point getTorsoOrigin() {
        return torsoOrigin;
    }

    public void setTorsoOrigin(double xx, double yy) {
        torsoOrigin.setLocation(xx, yy);
    }

    public Point getTorsoDiff() {
        return torsoDiff;
    }

    public void setTorsoDiff(double xx, double yy) {
        torsoDiff.setLocation(xx, yy);
    }

    public double getRadHead() {
        return radHead;
    }

    public void setRadHead(double d) {
        radHead = d;
    }

    public double getRadRightArm(int i) {
        return radRightArm[i];
    }

    public void setRadRightArm(int i, double d) {
        radRightArm[i] = d;
    }

    public double getRadLeftArm(int i) {
        return radLeftArm[i];
    }

    public void setRadLeftArm(int i, double d) {
        radLeftArm[i] = d;
    }

    public double getRadRightLeg(int i) {
        return radRightLeg[i];
    }

    public void setRadRightLeg(int i, double d) {
        radRightLeg[i] = d;
    }

    public double getRadLeftLeg(int i) {
        return radLeftLeg[i];
    }

    public void setRadLeftLeg(int i, double d) {
        radLeftLeg[i] = d;
    }

    public boolean getTorso_draggable() {
        return torso_draggable;
    }

    public void setTorso_draggable(boolean b) {
        torso_draggable = b;
    }

    public boolean getHead_draggable() {
        return head_draggable;
    }

    public void setHead_draggable(boolean b) {
        head_draggable = b;
    }

    public boolean getL_u_arm_draggable() {
        return l_u_arm_draggable;
    }

    public void setL_u_arm_draggable(boolean b) {
        l_u_arm_draggable = b;
    }

    public boolean getL_l_arm_draggable() {
        return l_l_arm_draggable;
    }

    public void setL_l_arm_draggable(boolean b) {
        l_l_arm_draggable = b;
    }

    public boolean getL_hand_draggable() {
        return l_hand_draggable;
    }

    public void setL_hand_draggable(boolean b) {
        l_hand_draggable = b;
    }

    public boolean getR_u_arm_draggable() {
        return r_u_arm_draggable;
    }

    public void setR_u_arm_draggable(boolean b) {
        r_u_arm_draggable = b;
    }

    public boolean getR_l_arm_draggable() {
        return r_l_arm_draggable;
    }

    public void setR_l_arm_draggable(boolean b) {
        r_l_arm_draggable = b;
    }

    public boolean getR_hand_draggable() {
        return r_hand_draggable;
    }

    public void setR_hand_draggable(boolean b) {
        r_hand_draggable = b;
    }

    public boolean getL_u_leg_draggable() {
        return l_u_leg_draggable;
    }

    public void setL_u_leg_draggable(boolean b) {
        l_u_leg_draggable = b;
    }

    public boolean getL_l_leg_draggable() {
        return l_l_leg_draggable;
    }

    public void setL_l_leg_draggable(boolean b) {
        l_l_leg_draggable = b;
    }

    public boolean getL_foot_draggable() {
        return l_foot_draggable;
    }

    public void setL_foot_draggable(boolean b) {
        l_foot_draggable = b;
    }

    public boolean getR_u_leg_draggable() {
        return r_u_leg_draggable;
    }

    public void setR_u_leg_draggable(boolean b) {
        r_u_leg_draggable = b;
    }

    public boolean getR_l_leg_draggable() {
        return r_l_leg_draggable;
    }

    public void setR_l_leg_draggable(boolean b) {
        r_l_leg_draggable = b;
    }

    public boolean getR_foot_draggable() {
        return r_foot_draggable;
    }

    public void setR_foot_draggable(boolean b) {
        r_foot_draggable = b;
    }

    public AffineTransform getHead_transform() {
        return head_transform;
    }

    public void setHead_transform(AffineTransform a) {
        head_transform = a;
    }

    public AffineTransform getLeft_arm_transform(int i) {
        return left_arm_transform[i];
    }

    public void setLeft_arm_transform(AffineTransform a, int i) {
        left_arm_transform[i] = a;
    }

    public AffineTransform getRight_arm_transform(int i) {
        return right_arm_transform[i];
    }

    public void setRight_arm_transform(AffineTransform a, int i) {
        right_arm_transform[i] = a;
    }

    public AffineTransform getLeft_leg_transform(int i) {
        return left_leg_transform[i];
    }

    public void setLeft_leg_transform(AffineTransform a, int i) {
        left_leg_transform[i] = a;
    }

    public AffineTransform getRight_leg_transform(int i) {
        return right_leg_transform[i];
    }

    public void setRight_leg_transform(AffineTransform a, int i) {
        right_leg_transform[i] = a;
    }

    public Point getLeftArmOrigin(int i) {
        return leftArmOrigin[i];
    }

    public void setLeftArmOrigin(double x, double y, int i) {
        leftArmOrigin[i].setLocation(x, y);
    }

    public Point getRightArmOrigin(int i) {
        return rightArmOrigin[i];
    }

    public void setRightArmOrigin(double x, double y, int i) {
        rightArmOrigin[i].setLocation(x, y);
    }

    public Point getLeftLegOrigin(int i) {
        return leftLegOrigin[i];
    }

    public void setLeftLegOrigin(double x, double y, int i) {
        leftLegOrigin[i].setLocation(x, y);
    }

    public Point getRightLegOrigin(int i) {
        return rightLegOrigin[i];
    }

    public void setRightLegOrigin(double x, double y, int i) {
        rightLegOrigin[i].setLocation(x, y);
    }

    public Point getHeadOrigin() {
        return headOrigin;
    }

    public void setHeadOrigin(double x, double y) {
        headOrigin.setLocation(x, y);
    }

    public Point getSrcs(int i) {
        return srcs[i];
    }

    public void setSrcs(double x, double y, int i) {
        srcs[i].setLocation(x, y);
    }

    public Point getPressed_pt() {
        return pressed_pt;
    }

    public void setPressed_pt(double x, double y) {
        pressed_pt.setLocation(x, y);
    }

    public double getLeft_upper_ratio() {
        return left_upper_ratio;
    }

    public void setLeft_upper_ratio(double d) {
        left_upper_ratio = d;
    }

    public double getLeft_lower_ratio() {
        return left_lower_ratio;
    }

    public void setLeft_lower_ratio(double d) {
        left_lower_ratio = d;
    }

    public double getRight_upper_ratio() {
        return right_upper_ratio;
    }

    public void setRight_upper_ratio(double d) {
        right_upper_ratio = d;
    }

    public double getRight_lower_ratio() {
        return right_lower_ratio;
    }

    public void setRight_lower_ratio(double d) {
        right_lower_ratio = d;
    }

    public int getTotal_frames() {
        return total_frames;
    }

    public void setTotal_frames(int i) {
        total_frames = i;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int i) {
        time = i;
    }

    public boolean getPlayback() {
        return playback;
    }

    public void setPlayback(boolean b) {
        playback = b;
    }

    public Pose getSaved_pose(int i) {
        return saved_poses.get(i);
    }

    public void setPose(Pose p) {
        // set origins
        torsoOrigin = p.origins[0];
        headOrigin = p.origins[1];
        leftArmOrigin[0] = p.origins[2];
        leftArmOrigin[1] = p.origins[3];
        leftArmOrigin[2] = p.origins[4];
        rightArmOrigin[0] = p.origins[5];
        rightArmOrigin[1] = p.origins[6];
        rightArmOrigin[2] = p.origins[7];
        leftLegOrigin[0] = p.origins[8];
        leftLegOrigin[1] = p.origins[9];
        leftLegOrigin[2] = p.origins[10];
        rightLegOrigin[0] = p.origins[11];
        rightLegOrigin[1] = p.origins[12];
        rightLegOrigin[2] = p.origins[13];

        // set rads
        radHead = p.rads[0];
        radLeftArm[0] = p.rads[1];
        radLeftArm[1] = p.rads[2];
        radLeftArm[2] = p.rads[3];
        radRightArm[0] = p.rads[4];
        radRightArm[1] = p.rads[5];
        radRightArm[2] = p.rads[6];
        radLeftLeg[0] = p.rads[7];
        radLeftLeg[1] = p.rads[8];
        radLeftLeg[2] = p.rads[9];
        radRightLeg[0] = p.rads[10];
        radRightLeg[1] = p.rads[11];
        radRightLeg[2] = p.rads[12];

        // set affine transforms
        head_transform = p.transforms[0];
        left_arm_transform[0] = p.transforms[1];
        left_arm_transform[1] = p.transforms[2];
        left_arm_transform[2] = p.transforms[3];
        right_arm_transform[0] = p.transforms[4];
        right_arm_transform[1] = p.transforms[5];
        right_arm_transform[2] = p.transforms[6];
        left_leg_transform[0] = p.transforms[7];
        left_leg_transform[1] = p.transforms[8];
        left_leg_transform[2] = p.transforms[9];
        right_leg_transform[0] = p.transforms[10];
        right_leg_transform[1] = p.transforms[11];
        right_leg_transform[2] = p.transforms[12];

        // set srcs
        srcs = p.srcs;

        // set ratios
        left_upper_ratio = p.ratios[0];
        left_lower_ratio = p.ratios[1];
        right_upper_ratio = p.ratios[2];
        right_lower_ratio = p.ratios[3];
    }

    public void saveFrame() {
        // private Point torsoOrigin, headOrigin;
        // private Point[] leftArmOrigin, rightArmOrigin;
        // private Point[] leftLegOrigin, rightLegOrigin;
        Point pt = new Point();
        pt.setLocation(torsoOrigin.getX(), torsoOrigin.getY());
        Point[] origins = new Point[]{pt, headOrigin,
                leftArmOrigin[0], leftArmOrigin[1], leftArmOrigin[2],
                rightArmOrigin[0], rightArmOrigin[1], rightArmOrigin[2],
                leftLegOrigin[0], leftLegOrigin[1], leftLegOrigin[2],
                rightLegOrigin[0], rightLegOrigin[1], rightLegOrigin[2]};

        // private double radHead;
        // private double[] radRightArm;
        // private double[] radLeftArm;
        // private double[] radRightLeg;
        // private double[] radLeftLeg;
        double[] rads = new double[]{radHead,
                radLeftArm[0], radLeftArm[1], radLeftArm[2],
                radRightArm[0], radRightArm[1], radRightArm[2],
                radLeftLeg[0], radLeftLeg[1], radLeftLeg[2],
                radRightLeg[0], radRightLeg[1], radRightLeg[2]};

        // private AffineTransform head_transform;
        // private AffineTransform[] left_arm_transform, right_arm_transform;
        // private AffineTransform[] left_leg_transform, right_leg_transform;
        AffineTransform[] transforms = new AffineTransform[]{head_transform,
                left_arm_transform[0], left_arm_transform[1], left_arm_transform[2],
                right_arm_transform[0], right_arm_transform[1], right_arm_transform[2],
                left_leg_transform[0], left_leg_transform[1], left_leg_transform[2],
                right_leg_transform[0], right_leg_transform[1], right_leg_transform[2]};

        Point[] src_pts = srcs;
        double[] ratios = new double[]{left_upper_ratio, left_lower_ratio, right_upper_ratio, right_lower_ratio};

        Pose p = new Pose(origins, rads, transforms, src_pts, ratios);
        saved_poses.add(p);
    }

    public void clearFrom(int from) {
        for (int i = from; i < saved_poses.size(); i++) {
            saved_poses.remove(i);
        }
        total_frames = from - 1;
        time = from - 1;
    }

    public void clearAll() {
        clearFrom(1);
        setPose(saved_poses.get(0));
    }

    public void update() {
        setChanged();
        notifyObservers();
    }

}
