package com.mjk.fastdfs.client;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/**
 * TrackerServer 工厂类，创建对象池需要 BasePooledObjectFactory 对象或子类
 *
 * @author ma-jk
 * @date 2018-05-11 11:21
 **/
public class TrackerServerFactory extends BasePooledObjectFactory<TrackerServer> {

    @Override
    public TrackerServer create() throws Exception {
        // TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        // return TrackerServer
        return trackerClient.getConnection();
    }

    @Override
    public PooledObject<TrackerServer> wrap(TrackerServer trackerServer) {
        return new DefaultPooledObject<>(trackerServer);
    }
}
