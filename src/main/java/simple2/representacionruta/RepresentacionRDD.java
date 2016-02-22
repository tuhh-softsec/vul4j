/*
 * Created on 11-ago-2003
 *
 */
 
package simple2.representacionruta;
import java.awt.Color;

import simple2.rutadedatos.BancoRegistros;
import simple2.rutadedatos.IRepresentacionRDD;
import simple2.rutadedatos.MicroInstruccion;
import simple2.rutadedatos.RegisterChangeListener;
import simple2.utilidades.Conversiones;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
import simple2.utilidades.Desensamblador;

/**
 * 
 * Esa clase se encarga de agrupar los elementos gráficos que componen 
 * el simulador visual de la ruta de datos.
 * Se encarga de ir activando y desactivando los elementos segun
 * las señales que recibe 
 */
	
public class RepresentacionRDD implements RegisterChangeListener,IRepresentacionRDD
{
	/**
	 * superficie del dibujo
	 */
		
	private InterfaceDibujo dibujo;
		
		
	private Bus busA = null;
	private Bus busB = null;
	private Bus busC = null;
	private IElementoDibujable bufferA = null;
	private IElementoDibujable bufferB = null;
	private IElementoDibujable mar = null;
	private IElementoDibujable mbr = null;
	private IElementoDibujable sh = null;
		
	private IElementoDibujable alu = null;
	private IElementoDibujable memoriappal = null;
	private IElementoDibujable cable_rd = null;
	private IElementoDibujable cable_wr = null;
	private IElementoDibujable cable_m0 = null;
	private IElementoDibujable cable_m1 = null;
	private IElementoDibujable[] regs = new CajaRegistro[16];

	private IElementoDibujable cable_l0 = null;
	private IElementoDibujable cable_l1 = null;
	private IElementoDibujable cable_a0 = null;
	private IElementoDibujable cable_f3_f0 = null;
	private IElementoDibujable cable_c = null;
	private IElementoDibujable cable_n = null;
	private IElementoDibujable cable_z = null;
	private IElementoDibujable cable_s2_s0 = null;
	private IElementoDibujable cable_mbr_mux = null;
	private IElementoDibujable cable_mar_ppal = null;
	private IElementoDibujable cable_mbr_ppal = null;
	private IElementoDibujable cable_bufferA_mux = null;
	private IElementoDibujable cable_mux_alu = null;
	private IElementoDibujable cable_alu_sh = null;
	private IElementoDibujable cable_bufferB_alu = null;
	private IElementoDibujable cable_bufferB_mar = null;
	private IElementoDibujable cable_sh_mbr = null;
	private IElementoDibujable cable_sh = null;
	private IElementoDibujable mux = null;
		
		
	private IElementoDibujable et_wr = null;
	private IElementoDibujable et_rd = null;
	private IElementoDibujable et_m0 = null;
	private IElementoDibujable et_m1 = null;
	private IElementoDibujable et_l0 = null;
	private IElementoDibujable et_l1 = null;
		
	private IElementoDibujable et_s0= null;		
	private IElementoDibujable et_a0 = null;		
	private IElementoDibujable et_f0 = null;
		
	private IElementoDibujable et_c = null;
	private IElementoDibujable et_n = null;
	private IElementoDibujable et_z = null;
		
	private IElementoDibujable et_busa = null;
	private IElementoDibujable et_busb = null;
	private IElementoDibujable et_busc = null;
		
	private IElementoDibujable etiqueta = null;
		
	private IElementoDibujable etiqueta_rmc_Inst = null;
	private IElementoDibujable etiqueta_rmc_AMUX = null;
	private IElementoDibujable etiqueta_rmc_COND = null;
	private IElementoDibujable etiqueta_rmc_SH = null;
	private IElementoDibujable etiqueta_rmc_MBR = null;
	private IElementoDibujable etiqueta_rmc_MAR = null;
	private IElementoDibujable etiqueta_rmc_RD = null;
	private IElementoDibujable etiqueta_rmc_WR = null;
	private IElementoDibujable etiqueta_rmc_ENC = null;
	private IElementoDibujable etiqueta_rmc_C = null;
	private IElementoDibujable etiqueta_rmc_B = null;
	private IElementoDibujable etiqueta_rmc_A = null;
	private IElementoDibujable etiqueta_rmc_ADDR = null;
	private IElementoDibujable etiqueta_rmc_FIR = null;
	private IElementoDibujable etiqueta_rmc_ALU = null;
	private IElementoDibujable etiqueta_rmc_NombInst =null;
		
	private int ciclo = 0;	
	/**
	 * Crea una instancia de la clase.
	 * @param dibujo Superficie sobre la que se dibujaran los objetos del simulador
	 */
	public RepresentacionRDD (InterfaceDibujo dibujo)
	{
		this.dibujo = dibujo;
		dibujo.setRepresentacionRDD(this);
	
		this.etiqueta = new Etiqueta (dibujo, 30, 10, "");
		this.etiqueta_rmc_Inst = new Etiqueta (dibujo, 10, 25, "");
		this.etiqueta_rmc_AMUX = new Etiqueta (dibujo, 10, 40, "");
		this.etiqueta_rmc_COND = new Etiqueta (dibujo, 10, 55, "");
		this.etiqueta_rmc_SH = new Etiqueta (dibujo, 10, 70, "");
		this.etiqueta_rmc_MBR = new Etiqueta (dibujo, 10, 85, "");
		this.etiqueta_rmc_MAR = new Etiqueta (dibujo, 10, 100, "");
		this.etiqueta_rmc_RD = new Etiqueta (dibujo, 10, 115, "");
		this.etiqueta_rmc_WR = new Etiqueta (dibujo, 10, 130, "");
		this.etiqueta_rmc_ENC = new Etiqueta (dibujo, 10, 145, "");
		this.etiqueta_rmc_C = new Etiqueta (dibujo, 10, 160, "");
		this.etiqueta_rmc_B = new Etiqueta (dibujo, 10, 175, "");
		this.etiqueta_rmc_A = new Etiqueta (dibujo, 10, 190, "");
		this.etiqueta_rmc_ADDR = new Etiqueta (dibujo, 10, 205, "");
		this.etiqueta_rmc_FIR = new Etiqueta (dibujo, 10, 220, "");
		this.etiqueta_rmc_ALU = new Etiqueta (dibujo, 10, 235, "");
		this.etiqueta_rmc_NombInst=new Etiqueta (dibujo,10,250, "");
		int x_deC1 = 350;
		int x_deC2 = 400;
		int y_deC = 436;
		int anchoRegistro = 110;
		int altoRegistro = 15;
		int separacionRegistros = 3;
		int yreg1 = 15;
		int y_deB = 310;
		int sepregA = 40;
		int sepAB = 30;
		int x_deA2 = x_deC2+anchoRegistro+sepregA;
		int x_deB2 = x_deC2+anchoRegistro+sepregA+sepAB;
		int y_deAlu = 390;
		int x_deAlu = 530;
		int anchoAlu = 80;
		int y_deMUX = 360;
		int y_deMAR = 330;
		int x_deMAR = 160;
		int ancho_MAR = 90;
		int y_deMBR = y_deMUX;

			
		this.et_busa = new Etiqueta (dibujo, x_deA2-25 , yreg1 - 5, "Bus A");
		this.et_busb = new Etiqueta (dibujo, x_deB2-10, yreg1, "Bus B");
		this.et_busc = new Etiqueta (dibujo, x_deC1, yreg1 - 5, "Bus C");

		Cable[] cablesDeC = new Cable[16];

		for (int i=0; i < 16; i++)
		{
			cablesDeC[i] = new CableUnidireccional (dibujo, new int[]{x_deC1,y_deC, x_deC1, yreg1+ 5 + (altoRegistro+ separacionRegistros)*i, x_deC2, yreg1+5 + (altoRegistro+ separacionRegistros)*i});
		}
		this.busC = new Bus (cablesDeC);
			
		Cable[] cablesDeA = new Cable[16];
			
		for (int i = 0; i < 16; i++)
		{
			cablesDeA[i] = new CableUnidireccional (dibujo, new int[]{x_deC2 + anchoRegistro, yreg1+5+(altoRegistro+ separacionRegistros)*i, x_deA2, yreg1+5+(altoRegistro+ separacionRegistros)*i, x_deA2, y_deB});
		}
		this.busA = new Bus (cablesDeA);
			
		Cable[] cablesDeB = new Cable[16];
			
		for (int i=0; i < 16; i++)
		{
			cablesDeB[i] = new CableUnidireccional (dibujo, new int[]{x_deC2 + anchoRegistro,yreg1+10+(altoRegistro+ separacionRegistros)*i, x_deB2, yreg1+10+(altoRegistro+ separacionRegistros)*i, x_deB2,y_deB});
		}
		this.busB = new Bus (cablesDeB);
			
		for (int i=0; i < 16; i++)
		{
			this.regs[i] = new CajaRegistro (dibujo, x_deC2, yreg1 +(altoRegistro+ separacionRegistros)*i,anchoRegistro,altoRegistro, BancoRegistros.getNombreRegistro(i));
		}			
			
		this.memoriappal = new CajaRegistro(dibujo,31,330,90,100,"Memoria");
		dibujo.dibujarTexto(Color.BLACK,34,380,"Principal");
		dibujo.dibujarTexto(Color.BLACK,34,420,"(2048x16)");
		this.cable_rd = new CableUnidireccional (dibujo, new int[]{65,320,65,330} );
		this.cable_wr = new CableUnidireccional (dibujo, new int[]{100,320,100,330} );
		this.et_rd = new Etiqueta (dibujo, 58,317, "RD");
		this.et_wr = new Etiqueta (dibujo, 93,317, "WR");
			
			
		this.bufferA = new CajaRegistro (dibujo, 450,y_deB,110,16, "BufferA 0000");
		this.bufferB = new CajaRegistro(dibujo,570,y_deB,110,16,"BufferB 0000");
		this.cable_l0 = new CableUnidireccional (dibujo, new int[]{440,y_deB + 8,450,y_deB+8} );
		this.cable_l1 = new CableUnidireccional (dibujo, new int[]{690,y_deB + 8,680,y_deB+8} );
			
		this.et_l0 = new Etiqueta (dibujo, 420, y_deB + 12 , "L0");
		this.et_l1 = new Etiqueta (dibujo, 700, y_deB + 12 , "L1");
			
			
		this.cable_bufferB_alu = new CableUnidireccional (dibujo, new int[]{580,y_deB+16,580,y_deAlu} );
		this.cable_bufferB_mar  = new CableUnidireccional (dibujo, new int[]{580,y_deB + 16,580,y_deMAR+8,x_deMAR + ancho_MAR,y_deMAR +8});
		this.cable_bufferA_mux = new CableUnidireccional (dibujo, new int[]{550,y_deB+16,550,y_deMUX} );
			
		this.mux = new CajaRegistro(dibujo,500,y_deMUX,60,16,"MUX");
		this.cable_a0 = new CableUnidireccional (dibujo, new int[]{510,y_deMUX+26,510,y_deMUX+16} );
		this.cable_mux_alu = new CableUnidireccional (dibujo, new int[]{550,y_deMUX+16,550,390} );
		this.cable_alu_sh = new CableUnidireccional (dibujo, new int[]{570,422,570,430} );
			
		this.et_a0 = new Etiqueta (dibujo, 500, y_deMUX + 40, "A0 0");
			
		this.mar = new CajaRegistro(dibujo, x_deMAR,y_deMAR,ancho_MAR,16,"MAR 0000");
		this.mbr = new CajaRegistro (dibujo, x_deMAR,y_deMUX,ancho_MAR,16,"MBR 0000");
			
		this.cable_m0 = new CableUnidireccional (dibujo, new int[]{190,y_deMAR-10,190,y_deMAR} );
		this.cable_m1 = new CableUnidireccional (dibujo, new int[]{170,y_deMBR+26,170,y_deMBR+16} );
			
		this.et_m0 = new Etiqueta (dibujo, 182, y_deMAR - 12, "M0");
		this.et_m1 = new Etiqueta (dibujo, 162, y_deMBR + 39, "M1");
			
		this.cable_mbr_mux = new CableUnidireccional (dibujo, new int[]{x_deMAR + ancho_MAR,y_deMUX+8,500,y_deMUX+8} );
		this.cable_mar_ppal = new CableUnidireccional (dibujo, new int[]{x_deMAR,y_deMAR+8,121,y_deMAR+8} );
		this.cable_mbr_ppal = new CableBidireccional (dibujo, new int[]{121,y_deMUX+8,x_deMAR,y_deMUX+8} );		
		
		this.sh = new CajaRegistro(dibujo,x_deAlu,430,anchoAlu,16,"SH 0000");
		this.cable_s2_s0 = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu+10,438,x_deAlu+anchoAlu,438} );
		this.et_s0 = new Etiqueta (dibujo, x_deAlu+anchoAlu + 15, 443, "S2-S0 000");
			
		this.cable_sh_mbr  = new CableUnidireccional (dibujo, new int[]{x_deC1,y_deC,190,y_deC,190,y_deMUX+16});
		this.cable_sh = new Cable (dibujo, new int[] {x_deAlu, y_deC, x_deC1, y_deC});
			
			
		this.alu=new CajaRegistro(dibujo,x_deAlu,y_deAlu,anchoAlu,32,"ALU");
			
			
		this.cable_c = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,395,x_deAlu+anchoAlu+10,395} );
		this.cable_n = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,405,x_deAlu+anchoAlu+10,405} );
		this.cable_z = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,415,x_deAlu+anchoAlu+10,415} );
		this.cable_f3_f0 = new CableUnidireccional (dibujo, new int[]{x_deAlu-10,415,x_deAlu,415} );
		this.et_f0 = new Etiqueta (dibujo, x_deAlu - 75, 420, "F3-F0 0000");
		this.et_c = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 400, "C 0");
		this.et_n = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 410, "N 0");
		this.et_z = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 420, "Z 0");
			
		actualizarTodo();
	}

	/**
	 * Vuelve a dibujar todos los elementos en la 
	 * superficie de dibujo.
	 */
	public void actualizarTodo()
	{
		this.dibujo.clean();
		this.etiqueta.repintar();
		this.etiqueta_rmc_Inst.repintar();
		this.etiqueta_rmc_AMUX.repintar();
		this.etiqueta_rmc_COND.repintar();
		this.etiqueta_rmc_SH.repintar();
		this.etiqueta_rmc_MBR.repintar();
		this.etiqueta_rmc_MAR.repintar();
		this.etiqueta_rmc_RD.repintar();
		this.etiqueta_rmc_WR.repintar();
		this.etiqueta_rmc_ENC.repintar();
		this.etiqueta_rmc_C.repintar();
		this.etiqueta_rmc_B.repintar();
		this.etiqueta_rmc_A.repintar();
		this.etiqueta_rmc_ADDR.repintar();
		this.etiqueta_rmc_FIR.repintar();
		this.etiqueta_rmc_ALU.repintar();
		this.etiqueta_rmc_NombInst.repintar();
			
		this.et_busa.repintar();
		this.et_busb.repintar();
		this.et_busc.repintar();
			
		this.et_rd.repintar();
		this.et_wr.repintar();
		this.et_m0.repintar();
		this.et_m1.repintar();
		this.et_l0.repintar();
		this.et_l1.repintar();
			
		this.et_s0.repintar();
		this.et_a0.repintar();
		this.et_f0.repintar();
		this.et_c.repintar();
		this.et_n.repintar();
		this.et_z.repintar();
			
			
		for (int i=0; i < 16; i++) {
			this.regs[i].repintar();
		}
		this.busC.repintar();
		this.busA.repintar();
		this.busB.repintar();
		this.bufferA.repintar();
		this.bufferB.repintar();
		this.mar.repintar();
		this.mbr.repintar();
		this.sh.repintar();


		this.cable_m0.repintar();
		this.cable_m1.repintar();
		this.cable_l0.repintar();
		this.cable_l1.repintar();
		this.cable_a0.repintar();
		this.cable_f3_f0.repintar();
		this.cable_c.repintar();
		this.cable_n.repintar();
		this.cable_z.repintar();
		this.cable_s2_s0.repintar();
		this.cable_mbr_mux.repintar();
		this.cable_mar_ppal.repintar();
		this.cable_mbr_ppal.repintar();
		this.cable_bufferA_mux.repintar();
		this.cable_mux_alu.repintar();
		this.cable_alu_sh.repintar();
		this.cable_bufferB_mar.repintar();
		this.cable_bufferB_alu.repintar();
		this.cable_sh_mbr.repintar();
		this.cable_sh.repintar();
		this.memoriappal.repintar();
		this.cable_rd.repintar();
		this.cable_wr.repintar();			
		this.mux.repintar();			
		this.alu.repintar();
		this.dibujo.dibujarTexto(Color.BLACK,34,380,"Principal");
		this.dibujo.dibujarTexto(Color.BLACK,34,420,"(2048x16)");
		this.dibujo.refresh();
	}
		
		
	/**
	 * Activa los elementos activos durante el subciclo 1.
	 * @param mic La instruccion que se acaba de cargar.
	 */
	public void dibujarCiclo1 (MicroInstruccion mic, short rdc)
	{
		this.ciclo++;
		pintarMicro (mic);
		this.etiqueta.setText ("Ciclo: " + this.ciclo + " Subciclo 1");
		this.cable_sh.apagar();
		this.cable_sh_mbr.apagar();
		this.mbr.apagar();
		this.sh.apagar();
		this.busC.apagar();
		for (int i=0; i < 16; i++)
			this.regs[i].apagar();
		if ((rdc == 0) ||((rdc > 1019) && (rdc < 1023)))
		{
			this.etiqueta_rmc_NombInst.setText("NOMBRE=Cargando Instrucción...");
		}
		actualizarTodo();
	}
	
	/**
	 * Activa los elementos activos durante el subciclo 2.
	 * @param mic La microinstrucción actualmente en ejecución.Nos indica los registros de origen.
	 * @param regA El contenido de BufferA.
	 * @param regB El contenido de BufferB.
	 */
	public void dibujarCiclo2 (MicroInstruccion mic, short regA, short regB)
	{
		pintarMicro (mic);
		this.etiqueta.setText ("Ciclo: " + this.ciclo + " Subciclo 2");
		this.busA.encender (mic.getA());
		this.busB.encender (mic.getB());
			
		this.regs[mic.getA()].encender();
		this.regs[mic.getB()].encender();
			
		this.bufferA.setText ("BufferA " + Conversiones.toHexString(regA));
		this.bufferB.setText ("BufferB " + Conversiones.toHexString(regB));			
			
		this.bufferA.encender();
		this.bufferB.encender();
			
		actualizarTodo();

	}
		
	/**
	 * Activa los elementos activos durante el subciclo 3.
	 * @param mic La microinstrucción en ejecución.
	 * @param vSH El valor del registro SH.
	 * @param vMAR El valor del registro MAR.
	 * @param vMBR El valor del registro MBR.
	 * @param valorC El valor de la salida C de la ALU.
	 * @param valorN El valor de la salida N de la ALU.
	 * @param valorZ El valor de la salida Z de la ALU.
	 */
	public void dibujarCiclo3(MicroInstruccion mic, short vSH, short vMAR,
		short vMBR, int valorC, int valorN, int valorZ )
	{
		pintarMicro (mic);
		this.etiqueta.setText("Ciclo: "+ this.ciclo + " Subciclo 3");
		this.busA.apagar();
		this.busB.apagar();			
		this.regs[mic.getA()].apagar();
		this.regs[mic.getB()].apagar();
		this.sh.encender();
			
		this.mbr.setText ("MBR " + Conversiones.toHexString (vMBR));
		this.mar.setText ("MAR " + Conversiones.toHexString (vMAR));
		this.sh.setText ("SH " + Conversiones.toHexString (vSH));
		this.et_s0.setText ("S2-S0 " + Conversiones.toBinaryString(mic.getSH(), 3));
		this.et_a0.setText ("A0 " + Conversiones.toBinaryString(mic.getAMUX(), 1));
		this.et_f0.setText ("F3-F0 " + Conversiones.toBinaryString(mic.getALU(), 4));
		this.et_c.setText ("C " + valorC);
		this.et_n.setText ("N " + valorN);
		this.et_z.setText ("Z " + valorZ);
			
			
		if (mic.getMAR() == 1)
		{
			//Activamos el cable que va de bufferB a MAR
			this.mar.encender();
			this.cable_bufferB_mar.encender();
		}
		if (mic.getAMUX() == 1)
		{
			this.cable_mbr_mux.encender();
			this.mbr.encender();
		}
		else
		{
			this.cable_bufferA_mux.encender();
		}
		this.cable_bufferB_alu.encender();
		this.cable_alu_sh.encender();
		this.alu.encender();
		this.mux.encender();
		this.cable_mux_alu.encender();

		actualizarTodo();
	}
		
	/**
	 * Activa los elelmentos activos durante el subciclo 4.
	 * @param mic La microinstruccion en ejecucion
	 * @param vMBR El valor del registro MBR
	 */	
	public void dibujarCiclo4(MicroInstruccion mic, short vMBR)
	{
		pintarMicro (mic);
		this.etiqueta.setText ("Ciclo: " + this.ciclo + " Subciclo 4");
		this.bufferA.apagar();
		this.bufferB.apagar();
		this.mar.apagar();
		this.mbr.apagar();
		this.mbr.setText ("MBR " + Conversiones.toHexString (vMBR));
		this.cable_bufferB_mar.apagar();
		this.cable_bufferB_alu.apagar();
		this.alu.apagar();
		this.mux.apagar();
		this.cable_bufferA_mux.apagar();
		this.cable_alu_sh.apagar();
		this.cable_mux_alu.apagar();
		this.cable_mbr_mux.apagar();
		//Escribir el dato de salida en la ALU.
		//Activar los cables de salida necesarios.
		if (mic.getENC() == 1)
		{
			this.busC.encender(mic.getC());
			this.regs[mic.getC()].encender();
		}
		if (mic.getMBR() == 1)
		{
			//Activamos el cable que va de SH a MBR
			this.cable_sh_mbr.encender();
			this.mbr.encender();
		}
		if ( (!(mic.getENC()==1)) && (!(mic.getMBR()==1) ) )
		{
			this.sh.apagar();
		}
		else
		{
			this.cable_sh.encender();
		}
		actualizarTodo();
	}
		
	/**
	 * Apaga todos los elementos
	 */
	public void clean()
	{			
		this.busC.apagar();
		this.busA.apagar();
		this.busB.apagar();
		this.bufferA.apagar();
		this.bufferB.apagar();
		this.mar.apagar();
		this.mbr.apagar();
		this.sh.apagar();

		this.cable_m0.apagar();
		this.cable_m1.apagar();
		this.cable_l0.apagar();
		this.cable_l1.apagar();
		this.cable_a0.apagar();
		this.cable_f3_f0.apagar();
		this.cable_c.apagar();
		this.cable_n.apagar();
		this.cable_z.apagar();
		this.cable_s2_s0.apagar();
		this.cable_mbr_mux.apagar();
		this.cable_mar_ppal.apagar();
		this.cable_mbr_ppal.apagar();
		this.cable_bufferA_mux.apagar();
		this.cable_mux_alu.apagar();
		this.cable_alu_sh.apagar();
		this.cable_bufferB_alu.apagar();
		this.cable_bufferB_mar.apagar();
		this.cable_sh_mbr.apagar();
		this.cable_sh.apagar();
		this.memoriappal.apagar();
		this.cable_rd.apagar();
		this.cable_wr.apagar();			
		this.mux.apagar();	
		this.alu.apagar();			
			
		for (int i=0; i < 16; i++)
			this.regs[i].apagar();			
		actualizarTodo();

	}
		
	/**
	 * apagar todos los elementos
	 */	
	public void detener ()
	{
		this.clean();
	}
		
	/**
	 * Se llama cuando cambia el contenido de un registros
	 * @param registro El registro que se ha cambiado 
	 * @param newValue El nuevo valor almacenado en registro
	 */
	public void registerChanged (int registro, short newValue)
	{
		String cadena=(Integer.toHexString(newValue)).toUpperCase();
		// TODO String.format
		if (newValue < 0) {
			cadena=cadena.substring(cadena.length()-4);
		}
		if(cadena.length()==0) {
			cadena="0000";
		}
		if(cadena.length()==1) {
			cadena="000"+cadena;
		}
		if(cadena.length()==2){
			cadena="00"+cadena;
		}
		if(cadena.length()==3){
			cadena="0"+cadena;
		}
		this.regs[registro].setText (BancoRegistros.getNombreRegistro(registro) + " " + cadena);
		if(registro==3){
			this.etiqueta_rmc_NombInst.setText ("NOMBRE=" + Desensamblador.desensamblar(newValue));	
		}
		
	}
		
	/**
	 * Se llama para inicializar el listener, pasandole un array con el 
	 * contenido de todos los registros.
	 * @param newValues Los valores almacenados en los registros.
	 */
	public void registerChanged (short[] newValues)
	{
		for (int i=0; i < newValues.length; i++)
		{
			registerChanged (i, newValues[i]);
		}
	}
	
	/**
	 * Nos muestra los valores que tiene en cada momento la microinstruccion
	 * @param mic Microinstruccion que vamos a analizar
	 */	
	private void pintarMicro (MicroInstruccion mic)
	{
		this.etiqueta_rmc_Inst.setText ("Inst = " + mic.toHexString());
		this.etiqueta_rmc_AMUX.setText ("AMUX="+ Conversiones.toBinaryString (mic.getAMUX(),1));
		this.etiqueta_rmc_COND.setText ("COND="+ Conversiones.toBinaryString (mic.getCOND(),3));
		this.etiqueta_rmc_SH.setText ("SH="  + Conversiones.toBinaryString (mic.getSH(),3));
		this.etiqueta_rmc_MBR.setText ("MBR=" + Conversiones.toBinaryString (mic.getMBR(),1));
		this.etiqueta_rmc_MAR.setText ("MAR=" + Conversiones.toBinaryString (mic.getMAR(),1));
		this.etiqueta_rmc_RD.setText ("RD="  + Conversiones.toBinaryString (mic.getRD(),1));
		this.etiqueta_rmc_WR.setText ("WR="  + Conversiones.toBinaryString (mic.getWR(),1));
		this.etiqueta_rmc_ENC.setText ("ENC=" + Conversiones.toBinaryString (mic.getENC(),1));
		this.etiqueta_rmc_C.setText ("C="   + Conversiones.toBinaryString (mic.getC(),4));
		this.etiqueta_rmc_B.setText ("B="   + Conversiones.toBinaryString (mic.getB(),4));
		this.etiqueta_rmc_A.setText ("A="   + Conversiones.toBinaryString (mic.getA(),4) );
		this.etiqueta_rmc_ADDR.setText ("ADDR="+ Conversiones.toBinaryString (mic.getADDR(),10));
		this.etiqueta_rmc_FIR.setText ("FIR=" + Conversiones.toBinaryString (mic.getFIR() , 1));
		this.etiqueta_rmc_ALU.setText ("ALU=" + Conversiones.toBinaryString (mic.getALU(),4));	
				
	}			
}
