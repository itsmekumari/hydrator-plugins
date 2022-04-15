/*
 * Copyright © 2022 Cask Data, Inc.
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
package io.cdap.plugin.common.stepsdesign;

import com.google.cloud.bigquery.BigQueryException;
import io.cdap.e2e.utils.BigQueryClient;
import io.cdap.e2e.utils.PluginPropertyUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.junit.Assert;
import stepsdesign.BeforeActions;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * GCP test hooks.
 */
public class TestSetupHooks {
  public static boolean firstFileSourceTestFlag = true;

  @Before(order = 1, value = "@FILE_SOURCE_TEST")
  public static void setFileAbsolutePath() {
    if (firstFileSourceTestFlag) {
      PluginPropertyUtils.addPluginProp("csvFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("csvFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("csvAllDataTypeFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("csvAllDataTypeFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("tsvFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("tsvFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("blobFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("blobFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("delimitedFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("delimitedFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("textFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("textFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("outputFieldTestFile", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("outputFieldTestFile")).getPath()).toString());
      PluginPropertyUtils.addPluginProp("readRecursivePath", Paths.get(TestSetupHooks.class.getResource
        ("/" + PluginPropertyUtils.pluginProp("readRecursivePath")).getPath()) + "/");
      firstFileSourceTestFlag = false;
    }
  }

  @Before(order = 1, value = "@BQ_SINK_TEST")
  public static void setTempTargetBQTableName() {
    String bqTargetTableName = "E2E_TARGET_" + UUID.randomUUID().toString().replaceAll("-", "_");
    PluginPropertyUtils.addPluginProp("bqTargetTable", bqTargetTableName);
    BeforeActions.scenario.write("BQ Target table name - " + bqTargetTableName);
  }

  @After(order = 1, value = "@BQ_SINK_TEST")
  public static void deleteTempTargetBQTable() throws IOException, InterruptedException {
    String bqTargetTableName = PluginPropertyUtils.pluginProp("bqTargetTable");
    try {
      BigQueryClient.dropBqQuery(bqTargetTableName);
      BeforeActions.scenario.write("BQ Target table - " + bqTargetTableName + " deleted successfully");
      PluginPropertyUtils.removePluginProp("bqTargetTable");
    } catch (BigQueryException e) {
      if (e.getMessage().contains("Not found: Table")) {
        BeforeActions.scenario.write("BQ Target Table " + bqTargetTableName + " does not exist");
      } else {
        Assert.fail(e.getMessage());
      }
    }
  }
}
