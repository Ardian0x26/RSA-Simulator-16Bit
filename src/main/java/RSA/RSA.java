package RSA;

import java.math.BigInteger;
import java.util.Scanner;

public class RSA {
    BigInteger N = new BigInteger("0");
    BigInteger phiOfN = new BigInteger("0");
    BigInteger p;
    BigInteger q;
    BigInteger e = new BigInteger("0");
    BigInteger d = new BigInteger("0");
    long keySize = 100000; //geht bis zu 100 000, danach schneidet die letzte funktion modexp ergebnisse ab, sodass was falsches rauskommt
    BigInteger t;
    BigInteger v;

    /*

     */
    public RSA() {
        creationOfPQ();
        this.StartRSA();
        inputEncodeDecode();


    }
    /*
    It the process, where you message is typed in and your encoded message to decode is typed in
     */
    private void inputEncodeDecode() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Gib dein zu verschluesselnden Code ein: \n");
        t = BigInteger.valueOf(Long.parseLong(sc.next()));
        v = this.verschluesseln();
        System.out.print("Verschluesselt mit RSA: " + v + "\n");
        System.out.print("Gib dein zu entschluesselnden Code ein: \n");
        BigInteger input = BigInteger.valueOf(Long.parseLong(sc.next()));
        System.out.print("Entschluesselt mit RSA: " + this.entschluesseln(input));
    }

    /*
    Has a loop, it calculates p and q, checks if prime, and checks if
    p and q are equal, if they are, they have to be recalculated and everything starts from beginning
     */
    private void creationOfPQ() {
        do {
            calulateRandomPQ();
            checkIfPQPrime();
        } while(p.equals(q));
        System.out.print("Passende Primzalhen gefunden!\n");
    }

    /*
    Checks if the calculated random numbers are prime, they have to be prime
     */
    private void checkIfPQPrime() {
        if(!isPrime(p)) {
           this.setPQZero();
           System.out.print("P oder Q keine Primzahl! Generiere neues P und Q\n");
            return;
        } else {
            if(!isPrime(q)) {
               this.setPQZero();
                System.out.print("Keine Primzahl! Generiere neues P und Q\n");
                return;
            }
        }
    }

    /*
    Creates 2 complete random numbers
     */
    private void calulateRandomPQ() {
        Long longP = (long)((Math.random() * (keySize - 3) +1) + 3);
        Long longQ = (long)((Math.random() * (keySize - 3) +1) + 3);
        p = new BigInteger(longP.toString());
        q = new BigInteger(longQ.toString());
        System.out.print(p + "\n");
        System.out.print(q + "\n");
        System.out.print("Prüfe Primzahlen!\n");
    }

    /*
    Checks if n is prime, it is prime when the conditions are fulfilled: for all numbers i < n it applies that ggT(n, i) = 1
     */
    private boolean isPrime(BigInteger n) {
        for(Integer i = 2; i < n.longValue(); i++) {
            if(ggT(n,new BigInteger(i.toString())).equals(new BigInteger("1"))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /*
    Euclid algorithm to find out ggT
     */
    private BigInteger ggT(BigInteger a, BigInteger b) {
        if (b.equals(new BigInteger("0"))) return a;
        return ggT(b, a.mod(b));
    }

    /*
    Just a help function for "checkIfPQPrime", it acts like a flag. It causes, that p and q are equal so p and q have to be recalculated
     */
    private void setPQZero() {
        p = new BigInteger("0");
        q = new BigInteger("0");
    }

    /*
    Generates a random number again and checks if ggT(e, Phi(N)) = 1, this has to be applied and is a condition for RSA to work
    If ggT(e,Phi(N) = 1 == false, then the loop will be applied again, if the condition is true, we go out of the loop with a break and keep e as it is
     */
    private void generateE() {
        while(true) {
            Long longE = (long)((Math.random() * (this.phiOfN.subtract(new BigInteger("2"))).longValue()) + 2);
            this.e = new BigInteger(longE.toString());

            if(ggT(e,this.phiOfN).equals(new BigInteger("1"))) {
                break;
            }
        }
    }
    /*
    Generates modular multiplicative inverse of e regarding mod Phi(N), if e and d are equal regenerate e and and calculate the inverse again
     */
    private void generateInverseD() { //inverses berechnen zu e : e * d = 1 mod phi(n)
        do {
            this.generateE();
            BigInteger biE = new BigInteger(String.valueOf(this.e));
            BigInteger biPhi = new BigInteger(String.valueOf(this.phiOfN));
            BigInteger biD;
            biD = biE.modInverse(biPhi);
            this.d = biD;
        } while (this.d.equals(this.e));

    }

    /*
    Calculate Phi(N)
     */
    private BigInteger phi(BigInteger n) {
        return (p.subtract(new BigInteger("1"))).multiply((q.subtract(new BigInteger("1"))));
    }

    /*
    Here, N, Phi(N) e and inverse d will be calculated, e is calculated and called by function "generateInverseD"
     */
    private void StartRSA() {
        N = p.multiply(q);
        this.phiOfN = phi(N);
        this.generateInverseD();
        System.out.print("\n-----------------------------------------------");
        System.out.print("\nN: " + N + "\n"); // N
        System.out.print("Phi(N): " + phiOfN + "\n"); //Phi(N)
        System.out.print("e: " + e + "\n"); // e
        System.out.print("d: " + d + "\n"); // inverses d
        System.out.print("Privater Schluessel: " + "(" + d + "," + N + ")\n");
        System.out.print("Oeffentlicher Schluessel: " + "(" + e + "," + N + ")\n");
        System.out.print("-----------------------------------------------");
        System.out.print("\n\n");
    }

    /*
    Encode using modular exponentation
     */
    private BigInteger verschluesseln() {
        BigInteger biE = new BigInteger(e.toString());
        BigInteger biT = t;
        BigInteger biN = new BigInteger(N.toString());
        BigInteger biResult = modexp(biT,biE,biN);
        //BigInteger biResult = biT.pow(e).mod(biN);
        return biResult;
    }

    /*
    Decode using modular exponentation
     */
    private BigInteger entschluesseln(BigInteger pV) {
        BigInteger biD = new BigInteger(d.toString());
        BigInteger biV = pV;
        BigInteger biN = new BigInteger(N.toString());
        BigInteger biResult = modexp(biV,biD,biN);
        //BigInteger biResult = biV.pow(d).mod(biN);
        return biResult;
    }

    /*
    Modular exponentation, this optimizes calculation of t^e mod N and v^d mod N
     */
    private BigInteger modexp(BigInteger pM, BigInteger pE, BigInteger pN) {
        BigInteger d = new BigInteger("1");
        String k = Integer.toBinaryString(pE.intValue());
        for(int i = 1;i <= k.length(); i++) {
            if(k.charAt(k.length() -i) == '1') {
                d = (d.multiply(pM)).mod(pN);
            }
            pM = (pM.multiply(pM)).mod(pN);
        }
        return d;
    }
}
