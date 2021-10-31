public class SumLongHex {
    public static void main(String[] args) {
        long sum = 0;
        for (int i = 0; i < args.length; i++) {
            sum += strSum(args[i]);
        }
        System.out.println(sum);
    }

    public static long strSum(String number) {
        long sum = 0;
        boolean prevNotSpace = false;
        String numstring = " " + number;
        int startInt = numstring.length();
        int endInt = numstring.length();
        for (int i = numstring.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(numstring.charAt(i))) {
                if (prevNotSpace) {
                    if (numstring.substring(startInt, endInt).startsWith("0x") || //Equals ignore case
                                numstring.substring(startInt, endInt).startsWith("0X")) {
                        sum += Long.parseUnsignedLong(numstring.substring(startInt + 2, endInt), 16);
                    } else {
                        sum += Long.parseLong(numstring.substring(startInt, endInt));
                    }
                }
                startInt = i;
                endInt = i;
                prevNotSpace = false;
            } else {
                startInt = i;
                prevNotSpace = true;
            }
        }
        return sum;
    }
}