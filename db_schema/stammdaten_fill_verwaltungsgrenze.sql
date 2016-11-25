insert into stammdaten.verwaltungsgrenze(gem_id, shape)
SELECT 
  verwaltungseinheit.id,
  gem_utm.geom
FROM 
  stammdaten.verwaltungseinheit
left join geo.gem_utm on verwaltungseinheit.id = gem_utm.ags;
