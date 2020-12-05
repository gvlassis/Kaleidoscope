public final class FFT {

    private FFT(){}

    public static Complex[] perform(Complex[] x){
        int N=x.length;
        int lgN= (int) Math.round(lg(N));

        Complex[] array1= new Complex[N];
        for(int n=0; n<N; n++){
            array1[reversePartOfInteger(n,lgN-1,0)]=new Complex("Cartesian",x[n].getReal(),x[n].getImaginary());
        }

        Complex[] array2= new Complex[N];
        for(int n=0; n<N; n++){
            array2[n]=new Complex("Cartesian",0,0);
        }


        int in1, in2;
        int out1, out2;
        int wingspan=1;
        int kaleidoscopesInStage= N/2;
        int butterfliesInKaleidoscope=1;
        for(int stage=lgN; stage>=1; stage--){
            int revStage=lgN-stage+1;
            int WIndex=(int) Math.pow(2,revStage);
            for(int kaleidoscopeID=0; kaleidoscopeID<kaleidoscopesInStage; kaleidoscopeID++){
                for(int butterflyID=0; butterflyID<butterfliesInKaleidoscope; butterflyID++){
                    in1=kaleidoscopeID*butterfliesInKaleidoscope*2+butterflyID;
                    in2=in1+wingspan;

                    out1=in1;
                    out2=in2;
                    butterfly(array1[in1],array1[in2],array2[out1],array2[out2],WIndex,butterflyID);
                }
            }

            for(int n=0; n<N; n++){
                array1[n]=new Complex("Cartesian",array2[n].getReal(),array2[n].getImaginary());
            }
            wingspan=2*wingspan;
            kaleidoscopesInStage=kaleidoscopesInStage/2;
            butterfliesInKaleidoscope=2*butterfliesInKaleidoscope;
        }



        Complex[] X=array2;

        return X;
    }


    public static void butterfly(Complex in1,Complex in2,Complex out1,Complex out2,int N,int power){
        Complex W=new Complex("Polar",1, -2*180*power/N);
        Complex in2xW=Complex.multiply(in2,W);

        out1.set("Cartesian",in1.getReal()+in2xW.getReal(),in1.getImaginary()+in2xW.getImaginary());
        out2.set("Cartesian",in1.getReal()-in2xW.getReal(),in1.getImaginary()-in2xW.getImaginary());
    }

    public static double lg(double x){
        return Math.log10(x)/Math.log10(2);
    }

    public static int reversePartOfInteger(int integer,int endingPoint,int startingPoint){
        String result="";

        for(int n=31; n>endingPoint; n--){
            result=result+getBit(integer,n);
        }
        for(int n=startingPoint; n<=endingPoint; n++){
            result=result+getBit(integer,n);
        }
        for(int n=startingPoint-1; n>=0; n--){
            result=result+getBit(integer,n);
        }
        return Integer.parseInt(result,2);

    }

    public static String getBit(int integer, int n){
        return String.valueOf((integer>>n)&1);
    }
}
