import java.util.*;
import java.lang.*;

public class Main
{
    public static int b = -1;
    public static int sizeb = -1;
    public static int cl = -1;
    public static int sizecl = -1;
    public static int sizebigcl = -1;
    public static int nb = -1;
    public static int sizenb = -1;
    public static int k = -1;
    public static int sizeset = -1;

    public static Scanner inp = new Scanner(System.in);

    public static class Block
    {
        public int tag;
        public String[] data;

        public Block()
        {
            this.tag = -1;
            this.data = new String[sizeb];

            for(int i=0;i<sizeb;i++)
            {
                data[i] = "*#JUNK#*";
            }
        }
    }

    public static class Instruct
    {
        public int tag;
        public int offset;
        public String[] data;

        public Instruct()
        {
            this.tag = -1;
            this.offset = -1;
            this.data = new String[sizeb];

            for(int i=0;i<sizeb;i++)
            {
                data[i] = "*#JUNK#*";
            }
        }

        public Block MakeBlock()
        {
            Block q = new Block();
            q.tag = this.tag;
            for(int i=0;i<sizeb;i++)
            {
                q.data[i] = this.data[i];
            }
            return q;
        }
    }

    public static void displaycache(Block[] cachesmall,int newsize)
    {
        Object[][] d = new Object[newsize+1][sizeb+1];
        d[0][0] = "Cache Line";
        for(int i=0;i<newsize+1;i++)
        {
            if(i!=0)
            {
                d[i][0] = Integer.toString(i);
            }
            for(int j=1;j<sizeb+1;j++)
            {
                if(i==0)
                {
                    d[i][j] = Integer.toString(j);
                }
                else
                {
                    d[i][j] = cachesmall[i-1].data[j-1];
                }
            }
        }

        int k = sizeb+1;
        String s = "";
        for(int i=0;i<k;i++)
        {
            s += "%15s";
        }
        for (final Object[] row : d)
        {
            System.out.format(s + "\n", row);
        }
    }

    public static void displaycachesetasso(Block[] cachesmall,int newsize,int linesinset)
    {
        int numset = 1;
        Object[][] d = new Object[newsize+1][sizeb+2];
        d[0][1] = "Cache Line";
        d[0][0] = "Set";
        for(int i=0;i<newsize+1;i++)
        {
            if(i!=0)
            {
                if((i-1)%linesinset==0)
                {
                    d[i][0] = Integer.toString(numset);
                    numset++;
                }
                else
                {
                    d[i][0] = " ";
                }

                d[i][1] = Integer.toString(i);
            }
            for(int j=2;j<sizeb+2;j++)
            {
                if(i==0)
                {
                    d[i][j] = Integer.toString(j);
                }
                else
                {
                    d[i][j] = cachesmall[i-1].data[j-2];
                }
            }
        }

        int k = sizeb+2;
        String s = "";
        for(int i=0;i<k;i++)
        {
            s += "%15s";
        }
        for (final Object[] row : d)
        {
            System.out.format(s + "\n", row);
        }
    }

    public static void main(String[] args)
    {
        try {
            //Taking Inputs
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.println("16 BIT MACHINE");
            System.out.println("--------------------------------------------------------------------------------------------\n");
            System.out.println("Enter the Size of one Block (in power of 2): ");
            b = inp.nextInt();
            sizeb = (int)(Math.pow(2,b));
            System.out.println("Size: " + sizeb + "\n\nEnter the Number of Cache Lines of Primary Cache (in power of 2): ");
            cl = inp.nextInt();
            sizecl = (int)(Math.pow(2,cl));
            sizebigcl = sizecl*2;
            System.out.println("Size: " + sizecl + "\n\nEnter the Number of Blocks wanted in Main memory (in power of 2): ");
            nb = inp.nextInt();
            sizenb = (int)(Math.pow(2,nb));
            System.out.println("Size: " + sizenb + "\n\n--------------------------------------------------------------------------------------------\n");

            Instruct Instruction = new Instruct();

            //Checking Errors
            if(b+nb>16)
            {
                System.out.println("\n\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                System.out.println("The instruction length (" + (b+nb) + ") will exceed 16 bits.");
                System.exit(0);
            }
            if(b<0 || cl<0 || nb<0)
            {
                throw new InputMismatchException("err");
            }

            //Creation
            int[] mainmem = new int[sizenb];
            Block[] mainmemory = new Block[sizenb];
            for(int i=0;i<sizenb;i++)
            {
                mainmem[i] = 0;
                Block q = new Block();
                mainmemory[i] = q;
            }

            System.out.println("Primary Cache:\n");
            Block[] cachesmall = new Block[sizecl];
            for(int i=0;i<sizecl;i++)
            {
                cachesmall[i] = new Block();
            }
            displaycache(cachesmall,sizecl);

            System.out.println("\n\nSecondary Cache:\n");
            Block[] cachebig = new Block[sizecl*2];
            for(int i=0;i<sizecl*2;i++)
            {
                cachebig[i] = new Block();
            }
            displaycache(cachebig,sizecl*2);

            //Menu
            int f = 0;
            do
            {
                System.out.println("\n--------------------------------------------------------------------------------------------\n");
                System.out.println("CHOOSE THE TYPE OF MEMORY MAPPING:\n      ");
                System.out.println("        1. Direct Mapping\n");
                System.out.println("        2. Associative Mapping (FIFO Replacement)\n");
                System.out.println("        3. n-way Set Associative Mapping (FIFO Replacement)\n\nOPTION: ");
                int op = inp.nextInt();

                if(op==1)
                {
                    Direct(mainmem,mainmemory,cachebig,cachesmall,Instruction);
                }
                else if(op==2)
                {
                    Asso(mainmem,mainmemory,cachebig,cachesmall,Instruction);
                }
                else if(op==3)
                {
                    System.out.println("\n--------------------------------------------------------------------------------------------\n");
                    System.out.println("Enter the number of sets (in power of 2) [Cannot be more than the number of Cache Lines]: ");
                    k = inp.nextInt();
                    sizeset = (int)Math.pow(2,k);

                    if(k>cl)
                    {
                        System.out.println("\n\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        System.out.println("The number of sets (" + sizeset + ") is greater than the number of Cache Lines in Primary Cache (" + sizecl + ").");
                        System.exit(0);
                    }
                    else
                    {
                        SetAsso(mainmem,mainmemory,cachebig,cachesmall,Instruction);
                    }
                }
                else
                {
                    throw new InputMismatchException("err");
                }

                System.out.println("\n--------------------------------------------------------------------------------------------\n");
                System.out.println("Do you wish to go back to the Menu? (Y/N): ");
                String ch = inp.next();

                if(ch.equals("Y")||ch.equals("y"))
                {
                    f = 0;
                }
                else if(ch.equals("N") ||ch.equals("n"))
                {
                    f = 1;
                }
                else
                {
                    throw new InputMismatchException("err");
                }

            }while (f==0);

            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("FINAL CACHE\n");
            System.out.println("Primary Cache:\n");
            displaycache(cachesmall,sizecl);
            System.out.println("\n\nSecondary Cache:\n");
            displaycache(cachebig,sizecl*2);
            System.out.println("\n--------------------------------------------------------------------------------------------");
        }
        catch(InputMismatchException err)
        {
            System.out.println("\n\nXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("The input is not in the correct format.");
            System.exit(0);
        }
    }

    public static void Input(int n,Instruct Instruction)
    {
        if(n==1)
        {
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("Enter the Block number to be written [Must be between and inclusive of 1 and " + sizenb + "]: ");
            Instruction.tag = inp.nextInt();
            if(Instruction.tag<1 || Instruction.tag>sizenb)
            {
                throw new InputMismatchException("err");
            }
            System.out.println("\nEnter the data to be written in the block (Single word) [Number of inputs = " + sizeb + "]: ");
            for(int i=0;i<sizeb;i++)
            {
                Instruction.data[i] = inp.next();
            }
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
        }
        else
        {
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("Enter the Block number to be read [Must be between and inclusive of 1 and " + sizenb + "]: ");
            Instruction.tag = inp.nextInt();
            if(Instruction.tag<1 || Instruction.tag>sizenb)
            {
                throw new InputMismatchException("err");
            }
            System.out.println("\nEnter the Block Offset to be read [Must be between and inclusive of 1 and " + sizeb + "]: ");
            Instruction.offset = inp.nextInt();
            if(Instruction.offset<1 || Instruction.offset>sizeb)
            {
                throw new InputMismatchException("err");
            }
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
        }
    }

    public static void Direct(int[] mainmem,Block[] mainmemory,Block[] cachebig,Block[] cachesmall,Instruct Instruction)
    {
        int f = 0;
        do
        {
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("CHOOSE THE TYPE OF INSTRUCTION:\n      ");
            System.out.println("        1. Write data\n");
            System.out.println("        2. Read data\n\nOPTION: ");
            int op = inp.nextInt();

            if(op==1)
            {
                Input(op,Instruction);

                mainmem[Instruction.tag-1] = 1;

                cachesmall[(Instruction.tag-1)%sizecl] = Instruction.MakeBlock();
                cachebig[(Instruction.tag-1)%sizebigcl] = Instruction.MakeBlock();
                mainmemory[Instruction.tag-1] = Instruction.MakeBlock();

                System.out.println("DATA WRITTEN IN MEMORY!");
            }
            else if(op==2)
            {
                Input(op,Instruction);

                if(cachesmall[(Instruction.tag-1)%sizecl].tag==Instruction.tag)
                {
                    System.out.println("DATA FOUND IN PRIMARY CACHE!\nCache Line: " + (((Instruction.tag-1)%sizecl)+1));
                    System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachesmall[(Instruction.tag-1)%sizecl].data[Instruction.offset-1]));
                }
                else
                {
                    System.out.println("CACHE MISS IN PRIMARY CACHE!");
                    if(cachebig[(Instruction.tag-1)%sizebigcl].tag==Instruction.tag)
                    {
                        System.out.println("DATA FOUND IN SECONDARY CACHE!\nCache Line: " + (((Instruction.tag-1)%sizebigcl)+1));
                        System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachebig[(Instruction.tag-1)%sizebigcl].data[Instruction.offset-1]));

                        cachesmall[(Instruction.tag-1)%sizecl] = cachebig[(Instruction.tag-1)%sizebigcl];
                        System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nCache Line: " + (((Instruction.tag-1)%sizecl)+1));
                    }
                    else
                    {
                        System.out.println("CACHE MISS IN SECONDARY CACHE!");
                        if(mainmem[Instruction.tag-1]==1)
                        {
                            System.out.println("DATA EXISTS IN MAIN MEMORY!");
                            System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (mainmemory[Instruction.tag-1].data[Instruction.offset-1]));

                            cachebig[(Instruction.tag-1)%sizebigcl] = mainmemory[Instruction.tag-1];
                            cachesmall[(Instruction.tag-1)%sizecl] = cachebig[(Instruction.tag-1)%sizebigcl];
                            System.out.println("DATA WRITTEN IN SECONDARY CACHE!\nCache Line: " + (((Instruction.tag-1)%sizebigcl)+1));
                            System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nCache Line: " + (((Instruction.tag-1)%sizecl)+1));
                        }
                        else
                        {
                            System.out.println("DATA DOES NOT EXIST IN MEMORY!");
                        }
                    }
                }
            }
            else
            {
                throw new InputMismatchException("err");
            }

            System.out.println("\nPrimary Cache:\n");
            displaycache(cachesmall,sizecl);
            System.out.println("\n\nSecondary Cache:\n");
            displaycache(cachebig,sizecl*2);

            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("Do you wish to give another instruction? (Y/N): ");
            String ch = inp.next();

            if(ch.equals("Y")||ch.equals("y"))
            {
                f = 0;
            }
            else if(ch.equals("N") ||ch.equals("n"))
            {
                f = 1;
            }
            else
            {
                throw new InputMismatchException("err");
            }

        }while (f==0);
    }

    public static void Asso(int[] mainmem,Block[] mainmemory,Block[] cachebig,Block[] cachesmall,Instruct Instruction)
    {
        int psmall = 0;
        int pbig = 0;
        int f = 0;
        do
        {
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("CHOOSE THE TYPE OF INSTRUCTION:\n      ");
            System.out.println("        1. Write data\n");
            System.out.println("        2. Read data\n\nOPTION: ");
            int op = inp.nextInt();

            if(op==1)
            {
                Input(op,Instruction);

                mainmem[Instruction.tag-1] = 1;

                cachesmall[psmall] = Instruction.MakeBlock();
                if(psmall==sizecl-1)
                {
                    psmall = 0;
                }
                else
                {
                    psmall++;
                }
                cachebig[pbig] = Instruction.MakeBlock();
                if(pbig==sizebigcl-1)
                {
                    pbig = 0;
                }
                else
                {
                    pbig++;
                }
                mainmemory[Instruction.tag-1] = Instruction.MakeBlock();

                System.out.println("DATA WRITTEN IN MEMORY!");
            }
            else if(op==2)
            {
                Input(op,Instruction);

                int smallcheck = -1;
                for(int i=0;i<sizecl;i++)
                {
                    if(cachesmall[i].tag==Instruction.tag)
                    {
                        smallcheck = i;
                        break;
                    }
                }
                if(smallcheck!=-1)
                {
                    System.out.println("DATA FOUND IN PRIMARY CACHE!\nCache Line: " + (smallcheck+1));
                    System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachesmall[smallcheck].data[Instruction.offset-1]));
                }
                else
                {
                    System.out.println("CACHE MISS IN PRIMARY CACHE!");
                    int bigcheck = -1;
                    for(int i=0;i<sizebigcl;i++)
                    {
                        if(cachebig[i].tag==Instruction.tag)
                        {
                            bigcheck = i;
                            break;
                        }
                    }
                    if(bigcheck!=-1)
                    {
                        System.out.println("DATA FOUND IN SECONDARY CACHE!\nCache Line: " + (bigcheck+1));
                        System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachebig[bigcheck].data[Instruction.offset-1]));

                        cachesmall[psmall] = cachebig[bigcheck];
                        System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nCache Line: " + (psmall+1));
                        if(psmall==sizecl-1)
                        {
                            psmall = 0;
                        }
                        else
                        {
                            psmall++;
                        }
                    }
                    else
                    {
                        System.out.println("CACHE MISS IN SECONDARY CACHE!");
                        if(mainmem[Instruction.tag-1]==1)
                        {
                            System.out.println("DATA EXISTS IN MAIN MEMORY!");
                            System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (mainmemory[Instruction.tag-1].data[Instruction.offset-1]));

                            cachebig[pbig] = mainmemory[Instruction.tag-1];
                            cachesmall[psmall] = cachebig[pbig];
                            System.out.println("DATA WRITTEN IN SECONDARY CACHE!\nCache Line: " + (pbig+1));
                            System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nCache Line: " + (psmall+1));
                            if(pbig==sizebigcl-1)
                            {
                                pbig = 0;
                            }
                            else
                            {
                                pbig++;
                            }
                            if(psmall==sizecl-1)
                            {
                                psmall = 0;
                            }
                            else
                            {
                                psmall++;
                            }
                        }
                        else
                        {
                            System.out.println("DATA DOES NOT EXIST IN MEMORY!");
                        }
                    }
                }
            }
            else
            {
                throw new InputMismatchException("err");
            }

            System.out.println("\nPrimary Cache:\n");
            displaycache(cachesmall,sizecl);
            System.out.println("\n\nSecondary Cache:\n");
            displaycache(cachebig,sizecl*2);

            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("Do you wish to give another instruction? (Y/N): ");
            String ch = inp.next();

            if(ch.equals("Y")||ch.equals("y"))
            {
                f = 0;
            }
            else if(ch.equals("N") ||ch.equals("n"))
            {
                f = 1;
            }
            else
            {
                throw new InputMismatchException("err");
            }

        }while (f==0);
    }

    public static void SetAsso(int[] mainmem,Block[] mainmemory,Block[] cachebig,Block[] cachesmall,Instruct Instruction)
    {
        int[] psmall = new int[sizeset];
        int[] pbig = new int[sizeset];
        for(int i=0;i<sizeset;i++)
        {
            psmall[i] = 0;
            pbig[i] = 0;
        }
        int f = 0;
        int targetset = -1;
        int targetlinesmall = -1;
        int targetlinebig = -1;
        int linesinsetsmall = (int)(sizecl/sizeset);
        int linesinsetbig = (int)(sizebigcl/sizeset);
        do
        {
            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("CHOOSE THE TYPE OF INSTRUCTION:\n      ");
            System.out.println("        1. Write data\n");
            System.out.println("        2. Read data\n\nOPTION: ");
            int op = inp.nextInt();

            if(op==1)
            {
                Input(op,Instruction);

                mainmem[Instruction.tag-1] = 1;
                targetset = (Instruction.tag-1)%sizeset;

                targetlinesmall = (targetset*linesinsetsmall) + psmall[targetset];
                cachesmall[targetlinesmall] = Instruction.MakeBlock();
                if(psmall[targetset]==linesinsetsmall-1)
                {
                    psmall[targetset] = 0;
                }
                else
                {
                    psmall[targetset]++;
                }

                targetlinebig = (targetset*linesinsetbig) + pbig[targetset];
                cachebig[targetlinebig] = Instruction.MakeBlock();
                if(pbig[targetset]==linesinsetbig-1)
                {
                    pbig[targetset] = 0;
                }
                else
                {
                    pbig[targetset]++;
                }
                mainmemory[Instruction.tag-1] = Instruction.MakeBlock();

                System.out.println("DATA WRITTEN IN MEMORY!");
            }
            else if(op==2)
            {
                Input(op,Instruction);
                targetset = (Instruction.tag-1)%sizeset;

                int smallcheck = -1;
                for(int i=(targetset*linesinsetsmall);i<(targetset*linesinsetsmall)+linesinsetsmall;i++)
                {
                    if(cachesmall[i].tag==Instruction.tag)
                    {
                        smallcheck = i;
                        break;
                    }
                }
                if(smallcheck!=-1)
                {
                    System.out.println("DATA FOUND IN PRIMARY CACHE!\nSet: " + (targetset+1) + "\nCache Line: " + (smallcheck+1));
                    System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachesmall[smallcheck].data[Instruction.offset-1]));
                }
                else
                {
                    System.out.println("CACHE MISS IN PRIMARY CACHE!");
                    int bigcheck = -1;
                    for(int i=(targetset*linesinsetbig);i<(targetset*linesinsetbig)+linesinsetbig;i++)
                    {
                        if(cachebig[i].tag==Instruction.tag)
                        {
                            bigcheck = i;
                            break;
                        }
                    }
                    if(bigcheck!=-1)
                    {
                        System.out.println("DATA FOUND IN SECONDARY CACHE!\nSet: " + (targetset+1) + "\nCache Line: " + (bigcheck+1));
                        System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (cachebig[bigcheck].data[Instruction.offset-1]));

                        targetlinesmall = (targetset*linesinsetsmall) + psmall[targetset];
                        cachesmall[targetlinesmall] = cachebig[bigcheck];
                        System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nSet: " + (targetset+1) + "\nCache Line: " + (targetlinesmall+1));
                        if(psmall[targetset]==linesinsetsmall-1)
                        {
                            psmall[targetset] = 0;
                        }
                        else
                        {
                            psmall[targetset]++;
                        }
                    }
                    else
                    {
                        System.out.println("CACHE MISS IN SECONDARY CACHE!");
                        if(mainmem[Instruction.tag-1]==1)
                        {
                            System.out.println("DATA EXISTS IN MAIN MEMORY!");
                            System.out.println("Block Offset: " + (Instruction.offset) + "\nData: " + (mainmemory[Instruction.tag-1].data[Instruction.offset-1]));

                            targetlinesmall = (targetset*linesinsetsmall) + psmall[targetset];
                            targetlinebig = (targetset*linesinsetbig) + pbig[targetset];
                            cachebig[targetlinebig] = mainmemory[Instruction.tag-1];
                            cachesmall[targetlinesmall] = cachebig[targetlinebig];
                            System.out.println("DATA WRITTEN IN SECONDARY CACHE!\nSet: " + (targetset+1) + "\nCache Line: " + (targetlinebig+1));
                            System.out.println("DATA WRITTEN IN PRIMARY CACHE!\nSet: " + (targetset+1) + "\nCache Line: " + (targetlinesmall+1));
                            if(pbig[targetset]==linesinsetbig-1)
                            {
                                pbig[targetset] = 0;
                            }
                            else
                            {
                                pbig[targetset]++;
                            }
                            if(psmall[targetset]==linesinsetsmall-1)
                            {
                                psmall[targetset] = 0;
                            }
                            else
                            {
                                psmall[targetset]++;
                            }
                        }
                        else
                        {
                            System.out.println("DATA DOES NOT EXIST IN MEMORY!");
                        }
                    }
                }
            }
            else
            {
                throw new InputMismatchException("err");
            }

            System.out.println("\nPrimary Cache:\n");
            displaycachesetasso(cachesmall,sizecl,linesinsetsmall);
            System.out.println("\n\nSecondary Cache:\n");
            displaycachesetasso(cachebig,sizecl*2,linesinsetbig);

            System.out.println("\n--------------------------------------------------------------------------------------------\n");
            System.out.println("Do you wish to give another instruction? (Y/N): ");
            String ch = inp.next();

            if(ch.equals("Y")||ch.equals("y"))
            {
                f = 0;
            }
            else if(ch.equals("N") ||ch.equals("n"))
            {
                f = 1;
            }
            else
            {
                throw new InputMismatchException("err");
            }

        }while (f==0);
    }
}