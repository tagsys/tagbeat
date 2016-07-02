# <strong>Tagbeat - Sensing Vibration through Backscatter Signals!</strong>



## <strong>Version</strong>

<table>
    <tr>
	    <td><strong>Version</strong></td>
    	<td><strong>Description</strong></td>
        <td><strong>Released Time</strong></td>
        <td><strong>Download</strong></td>
    </tr>
    <tr>
	    <td>1.0</td>
    	<td>This verison can recover the vibration signal.</td>
        <td>2016/6/1</td>
        <td>TDB</td>
    </tr>
</table>


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

## <strong>Usage</strong>

Please follow three simple steps:

1.Download tagbeat-xxx.zip and extract it to local disk.

2.Generate Fourier basis via the following matlab code.

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
Note: Please grab a coffee. It will take a very very long time to generate these basis. When finished, please copy "500.txt, 1000.txt, ..., 8000.txt" to  <code>PROJECT_ROOT_DIRECTORY/basis/</code>. Or, you can directly download these basis from here. Download and extract it to the <code>PROJECT_ROOT_DIRECTORY/</code>.

2.If you want to perform the sensing in real-time,
please start up TagSee and create a reader agent corresponding to your physical ImpinJ reader.
Otherwise, skip this step and test the benchmark samples offline.

3.Run the 'startup.sh' or 'startup.bat' in 'terminal' (Mac) or 'cmd' (Windows)
```bash
bash startup.sh
```
4.Access the following page: <a href="http://localhost:9001">http://localhost:9001</a>

## <strong>Compiling</strong>

If you would like to compile the source code, then you could do it as follows:

<strong>Prerequisite</strong>

1. Install Apache Maven. (refer to <a href="https://maven.apache.org/install.html" target="_blank">Maven</a> client)

2. Install Bower. (refer to <a href="http://bower.io/" target="_blank">Twitter Bower</a> client).

3. Install Git. (refer to <a href="https://git-scm.com/downloads" target="_blank">Git</a> client.)

<strong>Steps</strong>

1.Checkout the source code.
```
git clone https://github.com/tagsys/tagsee.git
```
2.Generate Fourier basis (Please refer to Usage#2).

3.Run the complie.sh, which will download the necessary Javascript dependencies.
```
bash comiple.sh
```
4.Run the project with Maven.
```
mvn run
```

## <strong>Notice</strong>

Tagbeat is built over TagSee. You can see the recovered vibration signals by either of the two methods:

1.[Offline] Use TagSee to collect the readings. Download the reading results to benchmark directory (<code>PROJECT_ROOT_DIRECTORY/history/</code>).
Finally, replay the readings offline through the Tagbeat dashboard.

2.[Online] Start TagSee and create a reader agent. Input the TagSee and agent IP in the Tagbeat dashboard.
Finally, start reading.

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

* 2016/6/27 - release the Tagbeat v1.0

## Acknowledgement

This project acknowledges significant help, feedback, suggestions and guidance from following people.
