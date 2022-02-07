package com.appgallabs.cloudmlplatform.datascience.codelabs;

import com.appgallabs.cloudmlplatform.datascience.service.ResettableStreamSplit;
import com.appgallabs.cloudmlplatform.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.transform.MathOp;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.transform.floattransform.FloatMathOpTransform;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.datavec.local.transforms.LocalTransformProcessRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Test;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.doubletransform.ConvertToDouble;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimpleLinearRegression {
    private static Logger logger = LoggerFactory.getLogger(SimpleLinearRegression.class);

    //@Test
    public void syntheticData() throws Exception{
        int seed = 123;
        double learningRate = 0.008;
        double momentum = 0.9;
        int numInputs = 1;
        int numOutputs = numInputs+1;
        int numHiddenNodes = 20;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        network.setListeners(new ScoreIterationListener(10));

        String storedData = IOUtils.resourceToString("dataScience/syntheticData.csv", StandardCharsets.UTF_8,
                Thread.currentThread().getContextClassLoader());
        ResettableStreamSplit inputStreamSplit = new ResettableStreamSplit(
                storedData);

        //This should be a parameter
        int batchSize = storedData.length();
        int nEpochs = 30;
        int labelIndex = 0;
        int possibleLabels = numOutputs;
        RecordReader rrTrain = new CSVRecordReader();
        rrTrain.initialize(inputStreamSplit);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rrTrain,
                batchSize, labelIndex, possibleLabels);

        network.fit(trainIter,nEpochs);

        Evaluation evaluation = network.evaluate(trainIter);
        System.out.println(evaluation);

        JsonObject json = JsonParser.parseString(evaluation.toJson()).getAsJsonObject();
        JsonUtil.print(json);
    }

    //@Test
    public void realData() throws Exception{
        String storedData = IOUtils.resourceToString("dataScience/realData.csv", StandardCharsets.UTF_8,
                Thread.currentThread().getContextClassLoader());
        ResettableStreamSplit inputStreamSplit = new ResettableStreamSplit(
                storedData);

        int seed = 123;
        double learningRate = 0.008;
        double momentum = 0.9;
        int numInputs = 9;
        int numOutputs = numInputs+1;
        int numHiddenNodes = 20;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        network.setListeners(new ScoreIterationListener(10));

        //This should be a parameter
        int batchSize = storedData.length();
        int nEpochs = 30;
        int labelIndex = 0;
        int possibleLabels = numOutputs;
        RecordReader rrTrain = new CSVRecordReader();
        rrTrain.initialize(inputStreamSplit);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rrTrain,
                batchSize, labelIndex, possibleLabels);

        network.fit(trainIter,nEpochs);

        Evaluation evaluation = network.evaluate(trainIter);
        System.out.println(evaluation);
    }

    //@Test
    public void validationData() throws Exception{
        String storedData = IOUtils.resourceToString("dataScience/california_housing_train.csv", StandardCharsets.UTF_8,
                Thread.currentThread().getContextClassLoader());

        int seed = 123;
        double learningRate = 0.008;
        double momentum = 0.9;
        int numInputs = 9;
        int numOutputs = numInputs+1;
        int numHiddenNodes = 20;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        network.setListeners(new ScoreIterationListener(10));

        //This should be a parameter
        StringBuilder csvBuilder = new StringBuilder();
        String[] rows = storedData.split("\n");
        int counter = 0;
        for(String row:rows){
            csvBuilder.append("0,"+row+"\n");
            /*if(counter == 1){
                break;
            }*/
            counter++;
        }
        storedData = csvBuilder.toString().trim();

        int batchSize = storedData.length();
        int nEpochs = 30;
        int labelIndex = 0;
        int possibleLabels = numOutputs;
        RecordReader rrTrain = new CSVRecordReader();
        ResettableStreamSplit inputStreamSplit = new ResettableStreamSplit(
                storedData);
        rrTrain.initialize(inputStreamSplit);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rrTrain,
                batchSize, labelIndex, possibleLabels);

        network.fit(trainIter,nEpochs);

        Evaluation evaluation = network.evaluate(trainIter);
        System.out.println(evaluation);
    }

    //@Test
    public void representationWithFeatureCross() throws Exception{
        DataSet ds = readCSVDataset();
        ArrayList<DataSet> DataSetList = new ArrayList<>();
        DataSetList.add(ds);

        plotDataset(DataSetList);

        MultiLayerNetwork net = fitStraightline(ds);

        // Get the min and max x values, using Nd4j
        NormalizerMinMaxScaler preProcessor = new NormalizerMinMaxScaler();
        preProcessor.fit(ds);
        int nSamples = 50;
        INDArray x = Nd4j.linspace(preProcessor.getMin().getInt(0), preProcessor.getMax().getInt(0), nSamples).reshape(nSamples, 1);
        INDArray y = net.output(x);
        DataSet modeloutput = new DataSet(x, y);
        DataSetList.add(modeloutput);

        //plot on by default
        plotDataset(DataSetList);

        Thread.sleep(60000);
    }

    private MultiLayerNetwork fitStraightline(DataSet ds) {
        int seed = 12345;
        int nEpochs = 200;
        double learningRate = 0.00001;
        int numInputs = 1;
        int numOutputs = 1;

        //
        // Hook up one input to the one output.
        // The resulting model is a straight line.
        //
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numOutputs)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS)
                        .activation(Activation.IDENTITY)
                        .nIn(numOutputs).nOut(numOutputs).build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        for (int i = 0; i < nEpochs; i++) {
            net.fit(ds);
        }

        return net;
    }

    private DataSet readCSVDataset() throws IOException, InterruptedException {
        String storedData = IOUtils.resourceToString("dataScience/california_housing_train.csv", StandardCharsets.UTF_8,
                Thread.currentThread().getContextClassLoader());
        Schema schema  =  new Schema.Builder()
                //.addColumnDouble("label")
                .addColumnDouble("longitude")
                .addColumnDouble("latitude")
                .addColumnsString("housing_median_age","total_rooms","total_bedrooms","population","households")
                .addColumnDouble("median_income")
                .addColumnFloat("median_house_value")
                .build();

        TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .removeColumns("latitude","housing_median_age","total_rooms","total_bedrooms","population","households","median_income")
                .transform(new FloatMathOpTransform("median_house_value",
                        MathOp.Divide,1000))
                .build();
        int batchSize = 17000;
        RecordReader rrTrain = new CSVRecordReader();
        ResettableStreamSplit inputStreamSplit = new ResettableStreamSplit(
                storedData);
        rrTrain.initialize(inputStreamSplit);
        LocalTransformProcessRecordReader rr =
                new LocalTransformProcessRecordReader(rrTrain, transformProcess);
        System.out.println(rr.nextRecord().toString());
        DataSetIterator iter = new RecordReaderDataSetIterator(rr, batchSize, 1, 1, true);
        return iter.next();
    }

    private void plotDataset(ArrayList<DataSet> DataSetList) {

        XYSeriesCollection c = new XYSeriesCollection();

        int dscounter = 1; //use to name the dataseries
        for (DataSet ds : DataSetList) {
            INDArray features = ds.getFeatures();
            INDArray outputs = ds.getLabels();

            int nRows = features.rows();
            XYSeries series = new XYSeries("S" + dscounter);
            for (int i = 0; i < nRows; i++) {
                series.add(features.getDouble(i), outputs.getDouble(i));
            }

            c.addSeries(series);
        }

        String title = "title";
        String xAxisLabel = "xAxisLabel";
        String yAxisLabel = "yAxisLabel";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean legend = false;
        boolean tooltips = false;
        boolean urls = false;
        //noinspection ConstantConditions
        JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, c, orientation, legend, tooltips, urls);
        JPanel panel = new ChartPanel(chart);

        JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setTitle("Training Data");

        f.setVisible(true);
    }
}
