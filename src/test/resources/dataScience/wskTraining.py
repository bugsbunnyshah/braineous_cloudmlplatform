#@title Import relevant modules
import math
import pandas as pd
import tensorflow as tf
from matplotlib import pyplot as plt
from io import StringIO
import random

dataset = StringIO("""flight_status,departure_airport,departure_scheduled,departure_estimated
        0.0,564165195,1314664959,1314664959
        1.0,1794863321,868836856,868836856
        1.0,2067876524,868836856,868836856
        1.0,2067876524,868836856,868836856
        1.0,2067876524,868836856,868836856
        0.0,1981761995,805584192,805584192
        0.0,1789797369,1856756901,1856756901
        0.0,1233325366,202416668,202416668
        0.0,1233325366,202416668,202416668
        0.0,1233325366,202416668,202416668
        0.0,1225424758,202416668,202416668
        1.0,1107868486,204305595,204305595
        0.0,1107868486,204305595,204305595
        1.0,1107868486,204305595,204305595
        1.0,1107868486,204305595,204305595
        0.0,1107868486,790161990,790161990
        0.0,1666362491,697964160,697964160
        1.0,1965471817,1233417570,1233417570
        1.0,1906466412,167772278,167772278
        0.0,1906466412,167772278,167772278
        1.0,1906466412,167772278,167772278
        0.0,1906466412,1629179081,1629179081
        0.0,1906466412,1851404381,1851404381
        1.0,1094017700,1743437645,1743437645
        1.0,2045137593,1706904328,1706904328
        0.0,1657550293,1263659059,1263659059
        0.0,1657550293,1263659059,1263659059
        1.0,1657550293,1964723637,1964723637
        0.0,1657550293,1150339803,1150339803
        0.0,1233325366,1150339803,1150339803
        1.0,1233325366,1150339803,1150339803
        1.0,1558582005,1358753368,1358753368
        1.0,2483,856936796,856936796
        0.0,2483,970256052,970256052
        0.0,2483,1120098314,1120098314
        1.0,1233325366,1150339803,1150339803
        0.0,1233325366,167772278,167772278
        1.0,1233325366,167772278,167772278
        1.0,1233325366,167772278,167772278
        0.0,1233325366,167772278,167772278
        0.0,1233325366,167772278,167772278
        1.0,1233325366,167772278,167772278
        1.0,1233325366,167772278,167772278
        0.0,1233325366,167772278,167772278
        0.0,1233325366,1629179081,1629179081
        1.0,1485060782,742678232,742678232
        0.0,1485060782,742678232,742678232
        1.0,1539021115,1856756901,1856756901
        1.0,1539021115,461175285,461175285
        1.0,986511735,1856756901,1856756901
        0.0,986511735,1856756901,1856756901
        0.0,986511735,1455642870,1455642870
        0.0,986511735,742678232,742678232
        0.0,986511735,742678232,742678232
        1.0,986511735,742678232,742678232
        0.0,986511735,742678232,742678232
        0.0,986511735,1443742810,1443742810
        0.0,986511735,353208549,353208549
        0.0,1297848953,2144807388,2144807388
        0.0,1297848953,2144807388,2144807388
        0.0,1297848953,2144807388,2144807388
        1.0,1297848953,2144807388,2144807388
        0.0,1297848953,2144807388,2144807388
        1.0,1297848953,1358753368,1358753368
        0.0,1297848953,1358753368,1358753368
        0.0,1297848953,1358753368,1358753368
        1.0,1297848953,1358753368,1358753368
        0.0,1297848953,1358753368,1358753368
        0.0,1297848953,1358753368,1358753368
        0.0,1297848953,2048740712,2048740712
        0.0,1297848953,2048740712,2048740712
        0.0,1297848953,2048740712,2048740712
        1.0,1297848953,2048740712,2048740712
        1.0,1297848953,2048740712,2048740712
        1.0,1297848953,2048740712,2048740712
        0.0,1297848953,1263659059,1263659059
        0.0,1297848953,1263659059,1263659059
        0.0,1297848953,1263659059,1263659059
        0.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        0.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        0.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        1.0,1297848953,1263659059,1263659059
        0.0,1297848953,2048740712,2048740712
        1.0,1297848953,2048740712,2048740712
        1.0,1297848953,2048740712,2048740712
        0.0,1297848953,449275225,449275225
        1.0,1297848953,449275225,449275225
        0.0,1297848953,533292300,533292300
        1.0,1297848953,533292300,533292300
        1.0,1297848953,533292300,533292300
        1.0,1297848953,533292300,533292300
        1.0,1297848953,1347676134,1347676134
        1.0,1297848953,1347676134,1347676134
        1.0,1297848953,1347676134,1347676134
        0.0,1297848953,1347676134,1347676134
    """)


def main(args):
    return {'payload': 'Hello-AI_Training8'}

#@title Define the functions that build and train a model
#@title Define the functions that build and train a model
def build_model(my_learning_rate):
    """Create and compile a simple linear regression model."""
    # Most simple tf.keras models are sequential.
    model = tf.keras.models.Sequential()

    # Describe the topography of the model.
    # The topography of a simple linear regression model
    # is a single node in a single layer.
    model.add(tf.keras.layers.Dense(units=1,
                                    input_shape=(1,)))

    # Compile the model topography into code that TensorFlow can efficiently
    # execute. Configure training to minimize the model's mean squared error.
    model.compile(optimizer=tf.keras.optimizers.RMSprop(lr=my_learning_rate),
                  loss="mean_squared_error",
                  metrics=[tf.keras.metrics.RootMeanSquaredError()])

    return model

def train_model(model, df, feature, label, epochs, batch_size):
    """Train the model by feeding it data."""

    # Feed the model the feature and the label.
    # The model will train for the specified number of epochs.
    history = model.fit(x=df[feature],
                        y=df[label],
                        batch_size=batch_size,
                        epochs=epochs)

    # Gather the trained model's weight and bias.
    trained_weight = model.get_weights()[0]
    trained_bias = model.get_weights()[1]

    # The list of epochs is stored separately from the rest of history.
    epochs = history.epoch

    # Isolate the error for each epoch.
    hist = pd.DataFrame(history.history)

    # To track the progression of training, we're going to take a snapshot
    # of the model's root mean squared error at each epoch.
    rmse = hist["root_mean_squared_error"]

    return trained_weight, trained_bias, epochs, rmse

#@title Define the plotting functions
def plot_the_model(trained_weight, trained_bias, feature, label):
    """Plot the trained model against 200 random training examples."""

    # Label the axes.
    plt.xlabel(feature)
    plt.ylabel(label)

    # Create a scatter plot from 200 random points of the dataset.
    random_examples = training_df.sample(n=33)
    plt.scatter(random_examples[feature], random_examples[label])

    # Create a red line representing the model. The red line starts
    # at coordinates (x0, y0) and ends at coordinates (x1, y1).
    #x0 = 0
    #y0 = trained_bias
    #x1 = random_examples[feature].size
    #y1 = trained_bias + (trained_weight * x1)
    #plt.plot([x0, x1], [y0, y1], c='r')

    xAxis = []
    yAxis = []
    for xValue in random_examples[feature]:
        xAxis.append(xValue)

    for yValue in random_examples[label]:
        value = trained_bias + trained_weight * int(yValue)
        print(value)
        yAxis.append(value)

    xMock = []
    for x in range(0, random_examples[feature].size):
        xMock.append(x)

    #print(xMock)
    #print(yAxis)
    #plt.plot(xValue, yAxis, c='r')

    # Render the scatter plot and the red line.
    plt.show()


def plot_the_loss_curve(epochs, rmse):
    """Plot a curve of loss vs. epoch."""

    plt.figure()
    plt.xlabel("Epoch")
    plt.ylabel("Root Mean Squared Error")

    plt.plot(epochs, rmse, label="Loss")
    plt.legend()
    plt.ylim([rmse.min()*0.97, rmse.max()])
    plt.show()

#-----------------------------------------------------------------------------
# The following lines adjust the granularity of reporting.
pd.options.display.max_rows = 10
pd.options.display.float_format = "{:.1f}".format

# Import the dataset.
#training_df = pd.read_csv("./wskTraining.csv")
training_df = pd.read_csv(dataset, sep=",")

# Print the first rows of the pandas DataFrame.
training_df.head()

# Get statistics on the dataset.
training_df.describe()

training_df[training_df.columns[0:2]]

print("********TRAINING_DATA********")
print(training_df)


# The following variables are the hyperparameters.
learning_rate = 0.001
epochs = 300
batch_size = 30

# Specify the feature and the label.
my_feature = ["departure_airport"] # the total number of rooms on a specific city block.
my_label="flight_status" # the median value of a house on a specific city block.
# That is, you're going to create a model that predicts house value based
# solely on total_rooms.

# Discard any pre-existing version of the model.
my_model = None


# Invoke the functions.
my_model = build_model(learning_rate)
print(my_model)

#train
weight, bias, epochs, rmse = train_model(my_model, training_df,
                                         my_feature, my_label,
                                         epochs, batch_size)

print("WEIGHT: ",weight)
print("BIAS: ",bias)