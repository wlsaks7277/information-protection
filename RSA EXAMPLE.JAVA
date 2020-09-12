/*
RSA 공개키 암호
- Certainty 가 1 -(1/2)200이상이 되는 p 와 q 생성
- 2048 비트 n 을 생성, e 와 d 생성
 - 임의의 대칭키 K 생성
 - 대칭키 K로 임의의 평문을 DES 암호화
 - RSA 공개키로 대칭키 K를 암호화해서 외부파일에 저장
 - 외부파일에서 암호화된 대칭키 불러와 RSA 복호화하여 대칭키 K 획득
 - 대칭키 K로 암호화된 평문 복구
*/
public class RSAC {
    public static void main(String[] args) throws NoSuchAlgorithmException,
   NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException,
   IllegalBlockSizeException, BadPaddingException, FileNotFoundException, IOException
   {
   
    BigInteger p,q,pMinus1, qMinus1, phi, e,d,n;
   
    do{//p와 q가 1024이면 n=pq이기 때문에
    p= new BigInteger(1024,200,new Random());//0~2^200-1 p생성
   
    }while(!p.isProbablePrime(200));
   
    do{
    q = new BigInteger(1024,200,new Random());//0~2^200-1 q생성
    }while(!q.isProbablePrime(200));
   
    pMinus1 = p.subtract(BigInteger.ONE);//p-1
    qMinus1 = q.subtract(BigInteger.ONE);//q-1
    n= p.multiply(q);//n=p*q
    phi=pMinus1.multiply(qMinus1);//파이 = (p-1) *(q-1)
   
    do{//(e,phi)=1
    e = new BigInteger(20 , new Random());
    }while(! (phi.gcd(e).equals(BigInteger.ONE)));
   
    d= e.modInverse(phi);// ed=1 mod (phi)
   
    //대칭키,평문 생성 및 암호화
    KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
    keyGenerator.init(new SecureRandom());
    SecretKey secretKey = keyGenerator.generateKey();
    byte[] plainText = "This is a Plaintext".getBytes();
   
    Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] cipherText = cipher.doFinal(plainText);
   
   
    //RSA 키 생성
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(n,e);//공개키
    RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(pubKeySpec);
   
    RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(n,d);//개인키
    RSAPrivateKey priKey = (RSAPrivateKey)keyFactory.generatePrivate(priKeySpec);
   
    //RSA 공개키로 대칭키 K 암호화
    Cipher Rcipher = Cipher.getInstance("RSA");
    Rcipher.init(Cipher.ENCRYPT_MODE, pubKey);
    byte[] Rcipherkey = Rcipher.doFinal(secretKey.getEncoded());
   
    //외부파일저장
    FileOutputStream fos = new FileOutputStream("save key");
    fos.write(Rcipherkey);
    fos.close();
   
    //파일로부터 암호화된 대칭키 불러오기
    FileInputStream fis = new FileInputStream("save key");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int theByte=0;
    while((theByte = fis.read())!=-1) {//“save key” 파일안에 있는 데이터를 읽음
    baos.write(theByte);//한글자씩 출력
    }
    fis.close();
    byte[] encodedPublicKey =baos.toByteArray();
    baos.close();
   
    //RSA 개인키로 복호화
    Rcipher.init(Cipher.DECRYPT_MODE,priKey);
    byte[] store = Rcipher.doFinal(encodedPublicKey);//대칭키
   
    //평문 복구
    SecretKeySpec deskey = new SecretKeySpec(store, "DES");
    SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
    SecretKey rDeskey = desFactory.generateSecret(deskey);
   
    cipher.init(Cipher.DECRYPT_MODE,rDeskey);
    byte[] storeText = cipher.doFinal(cipherText);
   
    System.out.println("평문 " + new String(plainText));
    System.out.println("복구된 평문 " + new String(storeText));
   
    }
   }