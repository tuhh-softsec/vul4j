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
	
		etiqueta = new Etiqueta (dibujo, 30, 10, "");
		etiqueta_rmc_Inst = new Etiqueta (dibujo, 10, 25, "");
		etiqueta_rmc_AMUX = new Etiqueta (dibujo, 10, 40, "");
		etiqueta_rmc_COND = new Etiqueta (dibujo, 10, 55, "");
		etiqueta_rmc_SH = new Etiqueta (dibujo, 10, 70, "");
		etiqueta_rmc_MBR = new Etiqueta (dibujo, 10, 85, "");
		etiqueta_rmc_MAR = new Etiqueta (dibujo, 10, 100, "");
		etiqueta_rmc_RD = new Etiqueta (dibujo, 10, 115, "");
		etiqueta_rmc_WR = new Etiqueta (dibujo, 10, 130, "");
		etiqueta_rmc_ENC = new Etiqueta (dibujo, 10, 145, "");
		etiqueta_rmc_C = new Etiqueta (dibujo, 10, 160, "");
		etiqueta_rmc_B = new Etiqueta (dibujo, 10, 175, "");
		etiqueta_rmc_A = new Etiqueta (dibujo, 10, 190, "");
		etiqueta_rmc_ADDR = new Etiqueta (dibujo, 10, 205, "");
		etiqueta_rmc_FIR = new Etiqueta (dibujo, 10, 220, "");
		etiqueta_rmc_ALU = new Etiqueta (dibujo, 10, 235, "");
		etiqueta_rmc_NombInst=new Etiqueta (dibujo,10,250, "");
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

			
		et_busa = new Etiqueta (dibujo, x_deA2-25 , yreg1 - 5, "Bus A");
		et_busb = new Etiqueta (dibujo, x_deB2-10, yreg1, "Bus B");
		et_busc = new Etiqueta (dibujo, x_deC1, yreg1 - 5, "Bus C");

		Cable[] cablesDeC = new Cable[16];

		for (int i=0; i < 16; i++)
		{
			cablesDeC[i] = new CableUnidireccional (dibujo, new int[]{x_deC1,y_deC, x_deC1, yreg1+ 5 + (altoRegistro+ separacionRegistros)*i, x_deC2, yreg1+5 + (altoRegistro+ separacionRegistros)*i});
		}
		busC = new Bus (cablesDeC);
			
		Cable[] cablesDeA = new Cable[16];
			
		for (int i = 0; i < 16; i++)
		{
			cablesDeA[i] = new CableUnidireccional (dibujo, new int[]{x_deC2 + anchoRegistro, yreg1+5+(altoRegistro+ separacionRegistros)*i, x_deA2, yreg1+5+(altoRegistro+ separacionRegistros)*i, x_deA2, y_deB});
		}
		busA = new Bus (cablesDeA);
			
		Cable[] cablesDeB = new Cable[16];
			
		for (int i=0; i < 16; i++)
		{
			cablesDeB[i] = new CableUnidireccional (dibujo, new int[]{x_deC2 + anchoRegistro,yreg1+10+(altoRegistro+ separacionRegistros)*i, x_deB2, yreg1+10+(altoRegistro+ separacionRegistros)*i, x_deB2,y_deB});
		}
		busB = new Bus (cablesDeB);
			
		for (int i=0; i < 16; i++)
		{
			regs[i] = new CajaRegistro (dibujo, x_deC2, yreg1 +(altoRegistro+ separacionRegistros)*i,anchoRegistro,altoRegistro, BancoRegistros.GetNombreRegistro(i));
		}			
			
		memoriappal = new CajaRegistro(dibujo,31,330,90,100,"Memoria");
		dibujo.dibujarTexto(Color.BLACK,34,380,"Principal");
		dibujo.dibujarTexto(Color.BLACK,34,420,"(2048x16)");
		cable_rd = new CableUnidireccional (dibujo, new int[]{65,320,65,330} );
		cable_wr = new CableUnidireccional (dibujo, new int[]{100,320,100,330} );
		et_rd = new Etiqueta (dibujo, 58,317, "RD");
		et_wr = new Etiqueta (dibujo, 93,317, "WR");
			
			
		bufferA = new CajaRegistro (dibujo, 450,y_deB,110,16, "BufferA 0000");
		bufferB = new CajaRegistro(dibujo,570,y_deB,110,16,"BufferB 0000");
		cable_l0 = new CableUnidireccional (dibujo, new int[]{440,y_deB + 8,450,y_deB+8} );
		cable_l1 = new CableUnidireccional (dibujo, new int[]{690,y_deB + 8,680,y_deB+8} );
			
		et_l0 = new Etiqueta (dibujo, 420, y_deB + 12 , "L0");
		et_l1 = new Etiqueta (dibujo, 700, y_deB + 12 , "L1");
			
			
		cable_bufferB_alu = new CableUnidireccional (dibujo, new int[]{580,y_deB+16,580,y_deAlu} );
		cable_bufferB_mar  = new CableUnidireccional (dibujo, new int[]{580,y_deB + 16,580,y_deMAR+8,x_deMAR + ancho_MAR,y_deMAR +8});
		cable_bufferA_mux = new CableUnidireccional (dibujo, new int[]{550,y_deB+16,550,y_deMUX} );
			
		mux = new CajaRegistro(dibujo,500,y_deMUX,60,16,"MUX");
		cable_a0 = new CableUnidireccional (dibujo, new int[]{510,y_deMUX+26,510,y_deMUX+16} );
		cable_mux_alu = new CableUnidireccional (dibujo, new int[]{550,y_deMUX+16,550,390} );
		cable_alu_sh = new CableUnidireccional (dibujo, new int[]{570,422,570,430} );
			
		et_a0 = new Etiqueta (dibujo, 500, y_deMUX + 40, "A0 0");
			
		mar = new CajaRegistro(dibujo, x_deMAR,y_deMAR,ancho_MAR,16,"MAR 0000");
		mbr = new CajaRegistro (dibujo, x_deMAR,y_deMUX,ancho_MAR,16,"MBR 0000");
			
		cable_m0 = new CableUnidireccional (dibujo, new int[]{190,y_deMAR-10,190,y_deMAR} );
		cable_m1 = new CableUnidireccional (dibujo, new int[]{170,y_deMBR+26,170,y_deMBR+16} );
			
		et_m0 = new Etiqueta (dibujo, 182, y_deMAR - 12, "M0");
		et_m1 = new Etiqueta (dibujo, 162, y_deMBR + 39, "M1");
			
		cable_mbr_mux = new CableUnidireccional (dibujo, new int[]{x_deMAR + ancho_MAR,y_deMUX+8,500,y_deMUX+8} );
		cable_mar_ppal = new CableUnidireccional (dibujo, new int[]{x_deMAR,y_deMAR+8,121,y_deMAR+8} );
		cable_mbr_ppal = new CableBidireccional (dibujo, new int[]{121,y_deMUX+8,x_deMAR,y_deMUX+8} );		
		
		sh = new CajaRegistro(dibujo,x_deAlu,430,anchoAlu,16,"SH 0000");
		cable_s2_s0 = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu+10,438,x_deAlu+anchoAlu,438} );
		et_s0 = new Etiqueta (dibujo, x_deAlu+anchoAlu + 15, 443, "S2-S0 000");
			
		cable_sh_mbr  = new CableUnidireccional (dibujo, new int[]{x_deC1,y_deC,190,y_deC,190,y_deMUX+16});
		cable_sh = new Cable (dibujo, new int[] {x_deAlu, y_deC, x_deC1, y_deC});
			
			
		alu=new CajaRegistro(dibujo,x_deAlu,y_deAlu,anchoAlu,32,"ALU");
			
			
		cable_c = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,395,x_deAlu+anchoAlu+10,395} );
		cable_n = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,405,x_deAlu+anchoAlu+10,405} );
		cable_z = new CableUnidireccional (dibujo, new int[]{x_deAlu+anchoAlu,415,x_deAlu+anchoAlu+10,415} );
		cable_f3_f0 = new CableUnidireccional (dibujo, new int[]{x_deAlu-10,415,x_deAlu,415} );
		et_f0 = new Etiqueta (dibujo, x_deAlu - 75, 420, "F3-F0 0000");
		et_c = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 400, "C 0");
		et_n = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 410, "N 0");
		et_z = new Etiqueta (dibujo, x_deAlu+anchoAlu+15, 420, "Z 0");
			
		ActualizarTodo();
	}

	/**
	 * Vuelve a dibujar todos los elementos en la 
	 * superficie de dibujo.
	 */
	public void ActualizarTodo()
	{
		dibujo.clean();
		etiqueta.Repintar();
		etiqueta_rmc_Inst.Repintar();
		etiqueta_rmc_AMUX.Repintar();
		etiqueta_rmc_COND.Repintar();
		etiqueta_rmc_SH.Repintar();
		etiqueta_rmc_MBR.Repintar();
		etiqueta_rmc_MAR.Repintar();
		etiqueta_rmc_RD.Repintar();
		etiqueta_rmc_WR.Repintar();
		etiqueta_rmc_ENC.Repintar();
		etiqueta_rmc_C.Repintar();
		etiqueta_rmc_B.Repintar();
		etiqueta_rmc_A.Repintar();
		etiqueta_rmc_ADDR.Repintar();
		etiqueta_rmc_FIR.Repintar();
		etiqueta_rmc_ALU.Repintar();
		etiqueta_rmc_NombInst.Repintar();
			
		et_busa.Repintar();
		et_busb.Repintar();
		et_busc.Repintar();
			
		et_rd.Repintar();
		et_wr.Repintar();
		et_m0.Repintar();
		et_m1.Repintar();
		et_l0.Repintar();
		et_l1.Repintar();
			
		et_s0.Repintar();
		et_a0.Repintar();
		et_f0.Repintar();
		et_c.Repintar();
		et_n.Repintar();
		et_z.Repintar();
			
			
		for (int i=0; i < 16; i++)
			regs[i].Repintar();
		busC.Repintar();
		busA.Repintar();
		busB.Repintar();
		bufferA.Repintar();
		bufferB.Repintar();
		mar.Repintar();
		mbr.Repintar();
		sh.Repintar();


		cable_m0.Repintar();
		cable_m1.Repintar();
		cable_l0.Repintar();
		cable_l1.Repintar();
		cable_a0.Repintar();
		cable_f3_f0.Repintar();
		cable_c.Repintar();
		cable_n.Repintar();
		cable_z.Repintar();
		cable_s2_s0.Repintar();
		cable_mbr_mux.Repintar();
		cable_mar_ppal.Repintar();
		cable_mbr_ppal.Repintar();
		cable_bufferA_mux.Repintar();
		cable_mux_alu.Repintar();
		cable_alu_sh.Repintar();
		cable_bufferB_mar.Repintar();
		cable_bufferB_alu.Repintar();
		cable_sh_mbr.Repintar();
		cable_sh.Repintar();
		memoriappal.Repintar();
		cable_rd.Repintar();
		cable_wr.Repintar();			
		mux.Repintar();			
		alu.Repintar();
		dibujo.dibujarTexto(Color.BLACK,34,380,"Principal");
		dibujo.dibujarTexto(Color.BLACK,34,420,"(2048x16)");
		dibujo.refresh();
	}
		
		
	/**
	 * Activa los elementos activos durante el subciclo 1.
	 * @param mic La instruccion que se acaba de cargar.
	 */
	public void DibujarCiclo1 (MicroInstruccion mic, short rdc)
	{
		ciclo++;
		PintarMicro (mic);
		etiqueta.setText ("Ciclo: " + ciclo + " Subciclo 1");
		cable_sh.Apagar();
		cable_sh_mbr.Apagar();
		mbr.Apagar();
		sh.Apagar();
		busC.Apagar();
		for (int i=0; i < 16; i++)
			regs[i].Apagar();
		if ((rdc == 0) ||((rdc > 1019) && (rdc < 1023)))
		{
			etiqueta_rmc_NombInst.setText("NOMBRE=Cargando Instrucción...");
		}
		ActualizarTodo();
	}
	
	/**
	 * Activa los elementos activos durante el subciclo 2.
	 * @param mic La microinstrucción actualmente en ejecución.Nos indica los registros de origen.
	 * @param regA El contenido de BufferA.
	 * @param regB El contenido de BufferB.
	 */
	public void DibujarCiclo2 (MicroInstruccion mic, short regA, short regB)
	{
		PintarMicro (mic);
		etiqueta.setText ("Ciclo: " + ciclo + " Subciclo 2");
		busA.Encender (mic.GetA());
		busB.Encender (mic.GetB());
			
		regs[mic.GetA()].Encender();
		regs[mic.GetB()].Encender();
			
		bufferA.setText ("BufferA " + Conversiones.ToHexString(regA));
		bufferB.setText ("BufferB " + Conversiones.ToHexString(regB));			
			
		bufferA.Encender();
		bufferB.Encender();
			
		ActualizarTodo();

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
	public void DibujarCiclo3(MicroInstruccion mic, short vSH, short vMAR,
		short vMBR, int valorC, int valorN, int valorZ )
	{
		PintarMicro (mic);
		etiqueta.setText("Ciclo: "+ ciclo + " Subciclo 3");
		busA.Apagar();
		busB.Apagar();			
		regs[mic.GetA()].Apagar();
		regs[mic.GetB()].Apagar();
		sh.Encender();
			
		mbr.setText ("MBR " + Conversiones.ToHexString (vMBR));
		mar.setText ("MAR " + Conversiones.ToHexString (vMAR));
		sh.setText ("SH " + Conversiones.ToHexString (vSH));
		et_s0.setText ("S2-S0 " + Conversiones.ToBinaryString(mic.GetSH(), 3));
		et_a0.setText ("A0 " + Conversiones.ToBinaryString(mic.GetAMUX(), 1));
		et_f0.setText ("F3-F0 " + Conversiones.ToBinaryString(mic.GetALU(), 4));
		et_c.setText ("C " + valorC);
		et_n.setText ("N " + valorN);
		et_z.setText ("Z " + valorZ);
			
			
		if (mic.GetMAR() == 1)
		{
			//Activamos el cable que va de bufferB a MAR
			mar.Encender();
			cable_bufferB_mar.Encender();
		}
		if (mic.GetAMUX() == 1)
		{
			cable_mbr_mux.Encender();
			mbr.Encender();
		}
		else
		{
			cable_bufferA_mux.Encender();
		}
		cable_bufferB_alu.Encender();
		cable_alu_sh.Encender();
		alu.Encender();
		mux.Encender();
		cable_mux_alu.Encender();

		ActualizarTodo();
	}
		
	/**
	 * Activa los elelmentos activos durante el subciclo 4.
	 * @param mic La microinstruccion en ejecucion
	 * @param vMBR El valor del registro MBR
	 */	
	public void DibujarCiclo4(MicroInstruccion mic, short vMBR)
	{
		PintarMicro (mic);
		etiqueta.setText ("Ciclo: " + ciclo + " Subciclo 4");
		bufferA.Apagar();
		bufferB.Apagar();
		mar.Apagar();
		mbr.Apagar();
		mbr.setText ("MBR " + Conversiones.ToHexString (vMBR));
		cable_bufferB_mar.Apagar();
		cable_bufferB_alu.Apagar();
		alu.Apagar();
		mux.Apagar();
		cable_bufferA_mux.Apagar();
		cable_alu_sh.Apagar();
		cable_mux_alu.Apagar();
		cable_mbr_mux.Apagar();
		//Escribir el dato de salida en la ALU.
		//Activar los cables de salida necesarios.
		if (mic.GetENC() == 1)
		{
			busC.Encender(mic.GetC());
			regs[mic.GetC()].Encender();
		}
		if (mic.GetMBR() == 1)
		{
			//Activamos el cable que va de SH a MBR
			cable_sh_mbr.Encender();
			mbr.Encender();
		}
		if ( (!(mic.GetENC()==1)) && (!(mic.GetMBR()==1) ) )
		{
			sh.Apagar();
		}
		else
		{
			cable_sh.Encender();
		}
		ActualizarTodo();
	}
		
	/**
	 * Apaga todos los elementos
	 */
	public void Clean()
	{			
		busC.Apagar();
		busA.Apagar();
		busB.Apagar();
		bufferA.Apagar();
		bufferB.Apagar();
		mar.Apagar();
		mbr.Apagar();
		sh.Apagar();

		cable_m0.Apagar();
		cable_m1.Apagar();
		cable_l0.Apagar();
		cable_l1.Apagar();
		cable_a0.Apagar();
		cable_f3_f0.Apagar();
		cable_c.Apagar();
		cable_n.Apagar();
		cable_z.Apagar();
		cable_s2_s0.Apagar();
		cable_mbr_mux.Apagar();
		cable_mar_ppal.Apagar();
		cable_mbr_ppal.Apagar();
		cable_bufferA_mux.Apagar();
		cable_mux_alu.Apagar();
		cable_alu_sh.Apagar();
		cable_bufferB_alu.Apagar();
		cable_bufferB_mar.Apagar();
		cable_sh_mbr.Apagar();
		cable_sh.Apagar();
		memoriappal.Apagar();
		cable_rd.Apagar();
		cable_wr.Apagar();			
		mux.Apagar();	
		alu.Apagar();			
			
		for (int i=0; i < 16; i++)
			regs[i].Apagar();			
		ActualizarTodo();

	}
		
	/**
	 * apagar todos los elementos
	 */	
	public void Detener ()
	{
		this.Clean();
	}
		
	/**
	 * Se llama cuando cambia el contenido de un registros
	 * @param registro El registro que se ha cambiado 
	 * @param newValue El nuevo valor almacenado en registro
	 */
	public void RegisterChanged (int registro, short newValue)
	{
		String cadena=(Integer.toHexString((int) newValue)).toUpperCase();
		if ((int) newValue < 0)
			cadena=cadena.substring(cadena.length()-4);
		if(cadena.length()==0) cadena="0000";
		if(cadena.length()==1) cadena="000"+cadena;
		if(cadena.length()==2) cadena="00"+cadena;
		if(cadena.length()==3) cadena="0"+cadena;
		regs[registro].setText (BancoRegistros.GetNombreRegistro(registro) + " " + cadena);
		if(registro==3){
			etiqueta_rmc_NombInst.setText ("NOMBRE=" + Desensamblador.Desensamblar(newValue));	
		}
		
	}
		
	/**
	 * Se llama para inicializar el listener, pasandole un array con el 
	 * contenido de todos los registros.
	 * @param newValues Los valores almacenados en los registros.
	 */
	public void RegisterChanged (short[] newValues)
	{
		for (int i=0; i < newValues.length; i++)
		{
			RegisterChanged (i, newValues[i]);
		}
	}
	
	/**
	 * Nos muestra los valores que tiene en cada momento la microinstruccion
	 * @param mic Microinstruccion que vamos a analizar
	 */	
	private void PintarMicro (MicroInstruccion mic)
	{
		etiqueta_rmc_Inst.setText ("Inst = " + mic.toHexString());
		etiqueta_rmc_AMUX.setText ("AMUX="+ Conversiones.ToBinaryString (mic.GetAMUX(),1));
		etiqueta_rmc_COND.setText ("COND="+ Conversiones.ToBinaryString (mic.GetCOND(),3));
		etiqueta_rmc_SH.setText ("SH="  + Conversiones.ToBinaryString (mic.GetSH(),3));
		etiqueta_rmc_MBR.setText ("MBR=" + Conversiones.ToBinaryString (mic.GetMBR(),1));
		etiqueta_rmc_MAR.setText ("MAR=" + Conversiones.ToBinaryString (mic.GetMAR(),1));
		etiqueta_rmc_RD.setText ("RD="  + Conversiones.ToBinaryString (mic.GetRD(),1));
		etiqueta_rmc_WR.setText ("WR="  + Conversiones.ToBinaryString (mic.GetWR(),1));
		etiqueta_rmc_ENC.setText ("ENC=" + Conversiones.ToBinaryString (mic.GetENC(),1));
		etiqueta_rmc_C.setText ("C="   + Conversiones.ToBinaryString (mic.GetC(),4));
		etiqueta_rmc_B.setText ("B="   + Conversiones.ToBinaryString (mic.GetB(),4));
		etiqueta_rmc_A.setText ("A="   + Conversiones.ToBinaryString (mic.GetA(),4) );
		etiqueta_rmc_ADDR.setText ("ADDR="+ Conversiones.ToBinaryString (mic.GetADDR(),10));
		etiqueta_rmc_FIR.setText ("FIR=" + Conversiones.ToBinaryString (mic.GetFIR() , 1));
		etiqueta_rmc_ALU.setText ("ALU=" + Conversiones.ToBinaryString (mic.GetALU(),4));	
				
	}			
}
