BUILD_DIR="build/"
# compile java
mkdir $BUILD_DIR
javac -d $BUILD_DIR *.java


# change working dir to build
pushd $BUILD_DIR

JAR_NAME="test.jar"

cp -r ../META-INF .
jar cMfv $JAR_NAME *

# copy in data set
cp ../words.txt .

echo "Starting Java program... (this will take a while)"
# Xmx is max
# Xms in inital
# Xss thread is stack size
java -Xmx5G -jar $JAR_NAME

# graph results
cp ../graph.py .
python graph.py

popd

echo "<*><*><*><*><*><*>"
echo "       DONE!      " 