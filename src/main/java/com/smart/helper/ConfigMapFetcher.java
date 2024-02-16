package com.smart.helper;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
 
public class ConfigMapFetcher {

    public static ConfigMap fetchConfigMapData(String namespace, String configMapName) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            ConfigMapList configMapList = client.configMaps().inNamespace(namespace).list();
            ConfigMap configMap = configMapList.getItems()
                    .stream()
                    .filter(cm -> cm.getMetadata().getName().equals(configMapName))
                    .findFirst()
                    .orElse(null);

            return configMap;
        }
    }
}
