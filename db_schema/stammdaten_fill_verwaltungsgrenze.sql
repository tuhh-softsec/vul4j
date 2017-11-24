INSERT INTO stamm.verwaltungsgrenze(gem_id, shape)
SELECT
  verwaltungseinheit.id,
  vg250_gem.geom
FROM
  stamm.verwaltungseinheit
JOIN geo.vg250_gem ON verwaltungseinheit.id = vg250_gem.ags;
