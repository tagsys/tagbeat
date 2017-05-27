# <strong>Tagbeat - Sensing Vibration through Backscatter Signals!</strong>

<a href="https://youtu.be/2QOaLTucS1M" target="_blank">Youtube</a>

## <strong>Version</strong>
| Version | Description | Time | Download |
|---------|-------------|------|----------|
| 0.2     |This verison can recover the vibration signal. | 2017/5/27 |  [tagbeat-v0.2](https://dl.dropboxusercontent.com/u/24821416/tagbeat/tagbeat-0.2.jar)|
| 0.1     |This verison can recover the vibration signal. | 2016/6/1 |  [tagbeat-v0.1](https://www.dropbox.com/s/k3b2k9ltf85zk9v/tagbeat-v0.1.zip?dl=0)|

##<strong>Fourier basis</strong>
| Version | Description | Time | Download |
|---------|-------------|------|----------|
|0.2      |Fourier basis for 500x500, 1000x1000, 2000x2000, ...., 8000x8000| 2016/7/7 | [Fourier basis](https://github.com/tagsys/tagbeat/tree/master/basis) |
|0.1      |Fourier basis for 500x500, 1000x1000, 2000x2000, ...., 8000x8000| 2016/7/7 | [Fourier basis](https://www.dropbox.com/s/90gmdpfg4lz9enb/basis.zip?dl=0) |


## <strong>Features</strong>

Traditional vibration inspection systems, equipped with separated sensing and communication modules,
are either very expensive (e.g., hundreds of dollars) and/or suffer from occlusion and narrow field of view (e.g., laser).
This paper brings forward a concept of ‘communication is sensing’,
which is to make sense of the world purely based on communication carrier rather than specialized sensors.
This project presents an RFID-based solution, Tagbeat, to inspect mechanical vibration using COTS RFID tags and readers.
Basic and useful feature list:

 * Over COTS RFID reader and tags.
 * Compressive reading.
 * Industrial measurement accuracy.
 * A large number of benchmark samples.
 

## <strong>Supported Platforms</strong>

* Windows/Mac/Linux + Java 8
* ImpinJ R420 Reader + LTK SDK + <a href="http://github.com/tagsys/tagsee" target="_blank">TagSee</a>

## <strong>Snapshot</strong>

 ![image](https://github.com/tagsys/tagbeat/blob/master/public/img/snapshot.png?raw=true)

## <strong>Usage</strong>

Please follow three simple steps:

(1) Download tagbeat-xxx.zip and extract it to local disk.

(2) Generate Fourier basis via the following matlab code.
```matlab
% Generate 500x500, 1000x1000, .... , 8000x8000 Fourier basis.
for N=[500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000]
    phi = fft(eye(N,N))/sqrt(N);

    file = fopen(strcat(num2str(N),'.txt'),'w');

    [M,N] = size(phi);
    for i=1:M
        for j=1:N
            fprintf(file,'%d %d %d %d\n',i,j,real(phi(i,j)),imag(phi(i,j)));
        end
    end
end
```
Note: Please grab a coffee to kill time. It will take a very very long time to generate these basis. When finished, please copy "500.txt, 1000.txt, ..., 8000.txt" to  <code>PROJECT_ROOT_DIRECTORY/basis/</code>.
Or, you can directly download these basis [here](https://github.com/tagsys/tagbeat/tree/master/basis) and then extract them to the <code>PROJECT_ROOT_DIRECTORY/basis</code>.

(3) If you want to perform the sensing in real-time,
please start up TagSee and create a reader agent corresponding to your physical ImpinJ reader.
Otherwise, skip this step and test the benchmark samples offline.

(4) Run the following command in terminal (Mac) or command (Windows) under the extracted directory.
```bash
java -jar tagbeat-xxx.jar
```
The tagbeat will try to connect tagsee. If you don't start tagsee, please just wait for the request timeout.

(5) Access the following page: [http://localhost:9001](http://localhost:9001).

## <strong>Compiling</strong>

If you would like to compile the source code, then you could do it as follows:

<strong>Prerequisite</strong>

 (1) Install Apache Maven. (refer to <a href="https://maven.apache.org/install.html" target="_blank">Maven</a> client)

 (2) Install Bower. (refer to <a href="http://bower.io/" target="_blank">Twitter Bower</a> client).

 (3) Install Git. (refer to <a href="https://git-scm.com/downloads" target="_blank">Git</a> client.)

<strong>Steps</strong>

 (1) Checkout the source code.
```
git clone https://github.com/tagsys/tagbeat.git
```
 (2) Generate Fourier basis (Please refer to Usage#Step 2).

 (3) Run the complie.sh, which will download the necessary Javascript dependencies.
```
bash comiple.sh
```
 (4) Install the project with Maven.
```
mvn install
```
 (5) Run the project with Maven.
```
mvn exec:java
```

## <strong>Notice</strong>

You can view the recovered vibration signals by either of the two methods:

(1) [Offline] Use TagSee to collect the readings. Download the reading results to benchmark directory (<code>PROJECT_ROOT_DIRECTORY/history/</code>).
Finally, replay the readings offline through the Tagbeat dashboard.

(2) [Online] Tagbeat is built over TagSee. Please start TagSee and create a reader agent. Input the TagSee and agent IP in the Tagbeat dashboard. Finally, start reading.

## Reference

If this project helps you, please help cite the following paper. Thanks very much.

```latex
@inproceedings{yang2016tagbeat,
  title={Making Sense of Mechanical Vibration Period with Sub-millisecond Accuracy Using Backscatter Signals},
  author={Yang, Lei and Yao, Li and Lin, Qiongzheng and Li, Xiang-Yang and Liu, Yunhao},
  booktitle={Proceedings of ACM MobiCom},
  year={2016}
}

```

## Changelog

* 2016/6/27 - release the Tagbeat v0.1
