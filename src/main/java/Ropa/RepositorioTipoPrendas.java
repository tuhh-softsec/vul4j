package Ropa;

import Excepciones.TipoDePrendaInvalidaException;

import java.util.List;

public class RepositorioTipoPrendas {

  private static RepositorioTipoPrendas INSTANCE;

  //CONSTRUCTOR
  private RepositorioTipoPrendas(){}

  public static RepositorioTipoPrendas instance(){

    if(INSTANCE == null){
      INSTANCE = new RepositorioTipoPrendas();
    }

    return INSTANCE;
  }



  private List<String> partesSuperiores;
  private List<String> partesInferiores;
  private List<String> calzados;
  private List<String> accesorios;

  public CategoriaPrenda buscarCategoria(String tipoPrenda){

    if(partesSuperiores.contains(tipoPrenda)) return CategoriaPrenda.PARTE_SUPERIOR;
    if(partesInferiores.contains(tipoPrenda)) return CategoriaPrenda.PARTE_INFERIOR;
    if(calzados.contains(tipoPrenda)) return CategoriaPrenda.CALZADO;
    if(accesorios.contains(tipoPrenda)) return CategoriaPrenda.ACCESORIO;
    else{
      throw new TipoDePrendaInvalidaException("El TIPO de prenda ingresado no es valido");
    }
  }



  //SETTERS
  public void setPartesSuperiores(List<String> partesSuperiores) {
    this.partesSuperiores = partesSuperiores;
  }

  public void setPartesInferiores(List<String> partesInferiores) {
    this.partesInferiores = partesInferiores;
  }

  public void setCalzados(List<String> calzados) {
    this.calzados = calzados;
  }

  public void setAccesorios(List<String> accesorios) {
    this.accesorios = accesorios;
  }

}
