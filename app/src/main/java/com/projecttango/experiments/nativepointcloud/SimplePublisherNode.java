/*
 * Copyright (C) 2014 Oliver Degener.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.projecttango.experiments.nativepointcloud;

import android.util.Log;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.message.Time;

import java.lang.Math;

import sensor_msgs.PointCloud2;
import static org.jboss.netty.buffer.ChannelBuffers.*;

import java.util.List;

import sensor_msgs.PointField;
import std_msgs.Float32;
import std_msgs.Header;

public class SimplePublisherNode extends AbstractNodeMain implements NodeMain {

    private static final String TAG = SimplePublisherNode.class.getSimpleName();
    public Publisher<sensor_msgs.PointCloud2> publisher1;
    public Publisher<geometry_msgs.PoseStamped> publisher2;
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SimplePublisher/TimeLoopNode");
    }

    final int[] seq = {0,0};

    public void publishPointCloud(String points){

        sensor_msgs.PointCloud2 pc2 = publisher1.newMessage();

        // obtain timestamp
        String[] str_data_token = points.split("\n");
        String[] str_timestamp_token = str_data_token[0].split("=");
        float timestamp = Float.parseFloat(str_timestamp_token[1]);

        // obtain coordinate streaming array
        String[] str_coord_token = str_data_token[1].split(",");

        // set height = 1, width = number of points
        int height = 1;
        int width = str_coord_token.length;
        pc2.setHeight(height);
        pc2.setWidth(width);

        // check for validity
        if(width % 3 != 0) {
            System.out.println("error parsing points");
        }
        // set step values
        int point_step = 4*3;
        int row_step = point_step*width;

        pc2.setPointStep(point_step);
        pc2.setRowStep(row_step);
        // set pointcloud data
        ChannelBuffer cb = buffer(row_step*height);
        pc2.setIsBigendian(false);
        List<PointField> pfl;
        for (int i = 0  ; i < width; i++) {
            // 3 coord indicates a point
            float coord = Float.parseFloat(str_coord_token[i]);
            cb.setFloat(i*4,coord);
            if(i%3 ==2){
                PointField pf = pc2.getFields().get(i%3);
                pf.setCount(width/3);
                pf.setDatatype((byte)7);
                pf.setOffset(i % 3);
                pf.setName("Point");
                pc2.getFields().set(i%3,pf);
            }
        }
        // set header
        Header head = pc2.getHeader();
        head.setSeq(seq[0]);
        seq[0]++;
        head.getStamp().secs = (int)timestamp;
        head.getStamp().nsecs = (int)((10^9)*(timestamp - (int)timestamp));
        head.setFrameId("0");

        publisher1.publish(pc2);

    }

    public void publishPose(String pose){

        String[] point_parsed = pose.split("\\%");


        // Obtain position, quaternion, timestamps of
        String[] pos_string = point_parsed[0].split(",");
        String[] quat_string = point_parsed[1].split(",");
        String timestamp_string = point_parsed[2];

        // Convert data strings into data

        float timestamp = Float.valueOf(timestamp_string).floatValue();
        float[] position = new float[3];
        for (int i = 0; i <= 2; i++){
            position[i] = Float.parseFloat(pos_string[i]);//Float.valueOf(pos_string[i]).floatValue();
        }
        float[] quaternion = new float[4];
        for (int i = 0; i <= 3; i++){
            quaternion[i] = Float.parseFloat(quat_string[i]);
        }

        geometry_msgs.PoseStamped str_pose = publisher2.newMessage();

        str_pose.getPose().getPosition().setX(position[0]);
        //Log.i(TAG, "Position X:\n" + position[0]);
        str_pose.getPose().getPosition().setY(position[1]);
        //Log.i(TAG, "Position Y:\n" + position[1]);
        str_pose.getPose().getPosition().setZ(position[2]);
        //Log.i(TAG, "Position Z:\n" + position[2]);
        str_pose.getPose().getOrientation().setX(quaternion[0]);
        //Log.i(TAG, "Quaternion X:\n" + quaternion[0]);
        str_pose.getPose().getOrientation().setY(quaternion[1]);
        //Log.i(TAG, "Quaternion Y:\n" + quaternion[1]);
        str_pose.getPose().getOrientation().setZ(quaternion[2]);
        //Log.i(TAG, "Quaternion Z:\n" + quaternion[2]);
        str_pose.getPose().getOrientation().setW(quaternion[3]);
        //Log.i(TAG, "Quaternion W:\n" + quaternion[3]);

        str_pose.getHeader().setSeq(seq[1]);
        //Log.i(TAG, "sequence:\n" + seq[0]);

        seq[1]++;
        str_pose.getHeader().setFrameId("0");

        double timestamp_s = Math.floor(timestamp);             // seconds     (s)
        double timestamp_ns = (timestamp - timestamp_s)*(10^9); // nanoseconds (ns)

        str_pose.getHeader().setStamp(new Time((int) timestamp_s, (int) timestamp_ns));
        //Log.i(TAG, "Timestamp_s:\n" + timestamp_s);
        //Log.i(TAG, "Timestamp_ns:\n" + timestamp_ns);
        // Publish message

        publisher2.publish(str_pose);

    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        //final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);
        //final Publisher<geometry_msgs.PoseStamped> publisher = connectedNode.newPublisher("android/orientation", "geometry_msgs/PoseStamped");
        publisher1 = connectedNode.newPublisher(GraphName.of("points"), PointCloud2._TYPE);
        publisher2 = connectedNode.newPublisher("android/orientation", "geometry_msgs/PoseStamped");


        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                Thread.sleep(1);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

}