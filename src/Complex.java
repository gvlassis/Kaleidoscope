public class Complex {

    private double real;
    private double imaginary;

    private double amplitude;
    private double phase;   //In degrees

    public void setReal(double passedReal){
        real=passedReal;

        amplitude=Math.sqrt( Math.pow(real,2)+Math.pow(imaginary,2) );

        if(real==0){
            if(imaginary>0){
                phase=90;
            }else if(imaginary<0){
                phase=-90;
            }else{
                phase=0;
            }
        }else{
            int n;
            if(real<0){
                n=-1;
            }else{
                n=0;
            }
            phase=Math.toDegrees( Math.atan(imaginary/real)+n*Math.toRadians(180));
        }
    }
    public double getReal(){
        return real;
    }


    public void setImaginary(double passedImaginary){
        imaginary=passedImaginary;

        amplitude=Math.sqrt( Math.pow(real,2)+Math.pow(imaginary,2) );

        if(real==0){
            if(imaginary>0){
                phase=90;
            }else if(imaginary<0){
                phase=-90;
            }else{
                phase=0;
            }
        }else{
            int n;
            if(real<0){
                n=-1;
            }else{
                n=0;
            }
            phase=Math.toDegrees( Math.atan(imaginary/real)+n*Math.toRadians(180));
        }

    }
    public double getImaginary(){
        return imaginary;
    }


    public void setAmplitude(double passedAmplitude){
        amplitude=passedAmplitude;

        real=amplitude*Math.cos(Math.toRadians(phase));
        imaginary=amplitude*Math.sin(Math.toRadians(phase));
    }
    public double getAmplitude(){
        return amplitude;
    }


    public void setPhase(double passedPhase){
        phase=passedPhase;

        real=amplitude*Math.cos(Math.toRadians(phase));
        imaginary=amplitude*Math.sin(Math.toRadians(phase));
    }
    public double getPhase(){
        return phase;
    }


    public Complex(String form, double in1, double in2) throws NoSuchFormException{

        if(form.equals("Cartesian")){
            real=in1;
            setImaginary(in2);
        }else if(form.equals("Polar")){
            amplitude=in1;
            setPhase(in2);
        }else{
            throw new NoSuchFormException("Complex form: "+form+" is invalid",null);
        }
    }

    public void set(String form, double in1, double in2) throws NoSuchFormException{

        if(form.equals("Cartesian")){
            setReal(in1);
            setImaginary(in2);
        }else if(form.equals("Polar")){
            setReal(in1);
            setPhase(in2);
        }else{
            throw new NoSuchFormException("Complex form: "+form+" is invalid",null);
        }
    }

    public String get(String form){
        String r;

        if(form.equals("Cartesian")){
            if(getImaginary()>=0){
                r=String.valueOf(getReal())+"+"+String.valueOf(getImaginary())+"j";
            }else{
                r=String.valueOf(getReal())+String.valueOf(getImaginary())+"j";
            }
        }else if(form.equals("Polar")){
            r=String.valueOf(getAmplitude())+"<"+String.valueOf(getPhase());
        }else{
            throw new NoSuchFormException("Complex form: "+form+" is invalid",null);
        }

        return r;
    }

    public static Complex add(Complex a, Complex b){
        return new Complex("Cartesian", a.getReal()+b.getReal(), a.getImaginary()+b.getImaginary());
    }

    public static Complex subtract(Complex a, Complex b){
        return new Complex("Cartesian", a.getReal()-b.getReal(), a.getImaginary()-b.getImaginary());
    }

    public static Complex multiply(Complex a, Complex b){
        return new Complex("Polar", a.getAmplitude()*b.getAmplitude(), a.getPhase()+b.getPhase());
    }

    public static Complex divide(Complex a, Complex b){
        return new Complex("Polar", a.getAmplitude()/b.getAmplitude(), a.getPhase()-b.getPhase());
    }



}
