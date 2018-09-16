package android.wickham.com.WekaModelLoad;

/*
 * Copyright (C) 2018 Mark Wickham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConverterUtils.DataSource sourceTrain, sourceTest = null;

        StringBuilder builder = new StringBuilder();
        TextView results = (TextView) findViewById(R.id.results);

        try {
            // Load the Test data
            builder.append("\n" + getCurrentTimeStamp() + ": Loading test data");
            sourceTest = new ConverterUtils.DataSource(getResources().openRawResource(R.raw.subject101_cleaned_5k));
            Instances dataTest = sourceTest.getDataSet();
            builder.append("\n" + getCurrentTimeStamp() + ": Test data load complete");

            // Set the class attribute (Label) as the first class
            dataTest.setClassIndex(0);

            Classifier rf;

            builder.append("\n" + getCurrentTimeStamp() + ": Loading model");
            // The following code utilizes the AssetManager to load the model from the app->src->main->assets folder
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("rf_i10_cross.model");
            //InputStream is = assetManager.open("rf_i100.model");
            rf = (Classifier) weka.core.SerializationHelper.read(is);

            // Alternatively, use the next line to load the model from the app->src->main->res->raw folder
            // rf = (Classifier) weka.core.SerializationHelper.read(getResources().openRawResource(R.raw.rf_i10_cross));

            builder.append("\n" + getCurrentTimeStamp() + ": Model load complete");
            Toast.makeText(this, "Model loaded.", Toast.LENGTH_SHORT).show();

            // Evaluate the classifier
            builder.append("\n" + getCurrentTimeStamp() + ": Starting classifier evaluation");
            Evaluation eval = new Evaluation(dataTest);
            eval.evaluateModel(rf, dataTest);
            builder.append("\n" + getCurrentTimeStamp() + ": Classifier evaluation complete");

            // Show the results
            builder.append("\n\nModel summary: " +  eval.toSummaryString());
            // Add the classifier capabilities
            builder.append("\nRF Model capabilities:\n" +  rf.getCapabilities().toString());
            results.setText((CharSequence) builder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }
}
