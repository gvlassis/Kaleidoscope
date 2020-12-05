import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application{
    private static final int BUFFER_SIZE_IN_FRAMES=1024;
    private static final int LAST_VALID_ELEM=BUFFER_SIZE_IN_FRAMES/2-1;
    private static final String FILENAME="waves.wav";
    private static final int PADDING=20;
    private static final int LOGO_SIZE=250;
    private static final String FONT="VisbyRoundCF-DemiBold.otf";
    private static final int FONT_SIZE=30;
    private static final int BAR_WIDTH=30;
    private static final int BAR_MAX_HEIGHT=300;
    private static final int BAR_GAP=10;
    private static final int NO_OF_BARS=100;
    private static final int BAR_INITIAL_X=0;
    private static final int BAR_INITIAL_Y=BAR_MAX_HEIGHT-BAR_WIDTH;
    private static final int visualizerWidth=(NO_OF_BARS-1)*(BAR_WIDTH+BAR_GAP)+BAR_WIDTH;
    private static final int visualizerHeight=BAR_MAX_HEIGHT;

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage1){
        VBox root=new VBox();
        root.setStyle("-fx-background-color: #333333; -fx-padding: "+PADDING+" "+PADDING+" "+PADDING+" "+PADDING);

        HBox hb1=new HBox();
        hb1.setStyle("-fx-spacing: "+PADDING);
        ImageView logoV=new ImageView();
        Image logoI=new Image(App.class.getResourceAsStream("img/Kaleidoscope logo.png"));
        logoV.setImage(logoI);
        logoV.setSmooth(true);
        logoV.setPreserveRatio(true);
        logoV.setFitHeight(LOGO_SIZE);
        hb1.getChildren().add(logoV);
        VBox vb1=new VBox();
        Font appFont=Font.loadFont(App.class.getResourceAsStream("font/"+FONT),FONT_SIZE);
        Label lbFile=new Label("File: "+FILENAME);
        lbFile.setFont(appFont);
        lbFile.setStyle("-fx-text-fill: #ffffff");
        Label lbN=new Label("N= "+BUFFER_SIZE_IN_FRAMES);
        lbN.setFont(appFont);
        lbN.setStyle("-fx-text-fill: #ffffff");
        Label lbChannels=new Label();
        lbChannels.setFont(appFont);
        lbChannels.setStyle("-fx-text-fill: #ffffff");
        Label lbBitDepth=new Label();
        lbBitDepth.setFont(appFont);
        lbBitDepth.setStyle("-fx-text-fill: #ffffff");
        Label lbEndianess=new Label();
        lbEndianess.setFont(appFont);
        lbEndianess.setStyle("-fx-text-fill: #ffffff");
        Label lbSamplingFrequency=new Label();
        lbSamplingFrequency.setFont(appFont);
        lbSamplingFrequency.setStyle("-fx-text-fill: #ffffff");
        vb1.getChildren().addAll(lbFile,lbN,lbChannels,lbBitDepth,lbEndianess,lbSamplingFrequency);
        hb1.getChildren().add(vb1);

        Label lbDevelopedBy=new Label("Developed by gvlassis");
        lbDevelopedBy.setFont(appFont);
        lbDevelopedBy.setStyle("-fx-text-fill: #ffffff");

        Pane visualizer=new Pane();

        root.getChildren().addAll(hb1,lbDevelopedBy,visualizer);

        Rectangle bar;

        for(int band=0; band<NO_OF_BARS; band++){
            bar=new Rectangle();

            bar.setStyle("-fx-fill: #ffffff");

            bar.setX(BAR_INITIAL_X+band*(BAR_WIDTH+BAR_GAP));
            bar.setY(BAR_INITIAL_Y);

            bar.setWidth(BAR_WIDTH);
            bar.setHeight(BAR_WIDTH);

            bar.setArcWidth(BAR_WIDTH);
            bar.setArcHeight(BAR_WIDTH);

            visualizer.getChildren().add(bar);
        }
        visualizer.setStyle("-fx-min-width: "+visualizerWidth+"; -fx-max-width: "+visualizerWidth+"; -fx-min-height: "+visualizerHeight+"; -fx-max-height: "+visualizerHeight);

        Scene scene1=new Scene(root);
        stage1.setTitle("Kaleidoscope");
        stage1.setScene(scene1);
        stage1.setResizable(false);
        stage1.show();

        Thread backThread=new Thread(new Runnable(){
            @Override
            public void run() {

                try(AudioInputStream ais= AudioSystem.getAudioInputStream(App.class.getResourceAsStream("snd/"+FILENAME))){
                    AudioFormat format=ais.getFormat();
                    int bytesPerFrame=format.getFrameSize();
                    int noOfChannels=format.getChannels();
                    int bitDepth=format.getSampleSizeInBits();
                    int channelSizeInBytes=bitDepth/8;
                    boolean isLittleEndian=!format.isBigEndian();
                    float samplingFrequency=format.getSampleRate();
                    float samplingPeriod=1/samplingFrequency;

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lbChannels.setText("Channels: "+noOfChannels);
                            lbBitDepth.setText("Bit depth: "+bitDepth+" bits");
                            if(isLittleEndian){
                                lbEndianess.setText("Endianess: Little Endian");
                            }else{
                                lbEndianess.setText("Endianess: Big Endian");
                            }
                            lbSamplingFrequency.setText("Sampling frequency: "+samplingFrequency+"Hz");
                        }
                    });

                    int bufferSizeInBytes=BUFFER_SIZE_IN_FRAMES*bytesPerFrame;
                    byte[] sndBytes=new byte[bufferSizeInBytes];

                    DataLine.Info sdlInfo = new DataLine.Info(SourceDataLine.class,format);
                    SourceDataLine sndLine= (SourceDataLine) AudioSystem.getLine(sdlInfo);
                    sndLine.open();
                    sndLine.start();

                    int numOfReadBytes;
                    while( (numOfReadBytes=ais.read(sndBytes)) != -1 ){
                        sndLine.write(sndBytes,0,numOfReadBytes);

                        //Exract frame samples from frames
                        Complex[] frSamples=new Complex[BUFFER_SIZE_IN_FRAMES];
                        int chSamplesRunSum;
                        byte[] bySamples=new byte[channelSizeInBytes];
                        byte[] bySamples4=new byte[4];
                        ByteBuffer bb= ByteBuffer.wrap(bySamples4);
                        int voidBytes=4-channelSizeInBytes;
                        byte voidByte;
                        if(isLittleEndian){
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                        }
                        for(int fr=0; fr<BUFFER_SIZE_IN_FRAMES; fr++){
                            chSamplesRunSum=0;
                            for(int ch=0; ch<noOfChannels; ch++){
                                for(int by=0; by<channelSizeInBytes; by++){
                                    bySamples[by]=sndBytes[fr*bytesPerFrame+ch*channelSizeInBytes+by];
                                }
                                if(isLittleEndian){
                                    if( Math.signum((double)bySamples[noOfChannels-1])<0 ){
                                        voidByte=(byte)0xff;
                                    }else{
                                        voidByte=(byte)0x00;
                                    }
                                    for(int i=0; i<voidBytes; i++){
                                        bySamples4[3-i]=voidByte;
                                    }
                                    for(int i=0; i<channelSizeInBytes; i++){
                                        bySamples4[i]=bySamples[i];
                                    }
                                }else{//isBigEndian
                                    if( Math.signum((double)bySamples[0])<0 ){
                                        voidByte=(byte)0xff;
                                    }else{
                                        voidByte=(byte)0x00;
                                    }
                                    for(int i=0; i<voidBytes; i++){
                                        bySamples4[i]=voidByte;
                                    }
                                    for(int i=0; i<channelSizeInBytes; i++){
                                        bySamples4[i+voidBytes]=bySamples[i];
                                    }
                                }

                                chSamplesRunSum=chSamplesRunSum+bb.getInt(0);
                            }
                            frSamples[fr]=new Complex("Cartesian",chSamplesRunSum/noOfChannels,0);

                        }
                        //Perform FFT on samples
                        Complex[] frSamplesDFT=FFT.perform(frSamples);

                        //Draw some of the DFT samples
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                double minAmplitude=frSamplesDFT[0].getAmplitude();
                                double maxAmplitude=frSamplesDFT[0].getAmplitude();
                                for(int i=0; i<BUFFER_SIZE_IN_FRAMES; i++){
                                    if(minAmplitude>frSamplesDFT[i].getAmplitude()){
                                        minAmplitude=frSamplesDFT[i].getAmplitude();
                                    }
                                    if(maxAmplitude<frSamplesDFT[i].getAmplitude()){
                                        maxAmplitude=frSamplesDFT[i].getAmplitude();
                                    }
                                }

                                double range=maxAmplitude-minAmplitude;
                                Rectangle bar;

                                for(int band=0; band<NO_OF_BARS; band++){
                                    int element=(LAST_VALID_ELEM/NO_OF_BARS)*band;
                                    double barHeight=frSamplesDFT[element].getAmplitude();    //Mathematically, I should multiply this with samplingPeriod. Since this is used for illustration purposes there is no point in doing so.
                                    double barHeightNormalized=((barHeight-minAmplitude)/range)*(BAR_MAX_HEIGHT-BAR_WIDTH)+BAR_WIDTH;    //barHeightNormalized is between barWidth barMaxHeight

//                                    double bandHeightInDB=20*Math.log10(bandHeightNormalized);

                                    bar= (Rectangle) visualizer.getChildren().get(band);

                                    bar.setY(BAR_MAX_HEIGHT-barHeightNormalized);

                                    bar.setHeight(barHeightNormalized);
                                }
                            }
                        });
                    }

                }catch(IOException | UnsupportedAudioFileException | LineUnavailableException e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        backThread.setDaemon(true);
        backThread.start();


    }
}
