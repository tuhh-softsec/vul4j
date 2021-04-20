package Ropa;

import Excepciones.TipoDePrendaInvalidaException;

import java.util.List;

public class RepositorioTipoPrendas {

  private static final RepositorioTipoPrendas INSTANCE = new RepositorioTipoPrendas();

  private RepositorioTipoPrendas(){}  //constructor

  public static RepositorioTipoPrendas instance(){
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
}
