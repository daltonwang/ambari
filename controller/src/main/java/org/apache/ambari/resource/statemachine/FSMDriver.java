/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.ambari.resource.statemachine;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.ambari.controller.Cluster;

import com.google.inject.Singleton;

@Singleton
public class FSMDriver implements FSMDriverInterface {
  private Map<String, ClusterFSM> clusters = 
      Collections.synchronizedMap(new HashMap<String,ClusterFSM>());
  @Override
  public ClusterFSM createCluster(Cluster cluster, int revision) 
      throws IOException {
    ClusterFSM clusterFSM = new ClusterImpl(cluster, revision);
    clusters.put(cluster.getName(), clusterFSM);
    return clusterFSM;
  }
  @Override
  public void startCluster(String clusterId) {
    ClusterFSM clusterFSM = clusters.get(clusterId);
    if (clusterFSM != null) {
      clusterFSM.activate();
    }
  }
  @Override
  public void stopCluster(String clusterId) {
    ClusterFSM clusterFSM = clusters.get(clusterId);
    if (clusterFSM != null) {
      clusterFSM.deactivate();
    }
  }
  @Override
  public void deleteCluster(String clusterId) {
    ClusterFSM clusterFSM = clusters.remove(clusterId);
    if (clusterFSM != null) { 
      clusterFSM.deactivate();
      clusterFSM.terminate();
    }
  }
  @Override
  public String getClusterState(String clusterId,
      long clusterDefinitionRev) {
    ClusterFSM clusterFSM = clusters.get(clusterId);
    if (clusterFSM != null) {
      return clusterFSM.getClusterState();
    }
    return null;
  }

  @Override
  public ClusterFSM getFSMClusterInstance(String clusterId) {
    return clusters.get(clusterId);
  }

}
