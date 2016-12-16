INSERT INTO stammdaten.verwaltungsgrenze(gem_id, shape)
SELECT
  verwaltungseinheit.id,
  gem_utm.geom
FROM
  stammdaten.verwaltungseinheit
JOIN geo.gem_utm ON verwaltungseinheit.id = gem_utm.ags;
