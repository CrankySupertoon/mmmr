SELECT
new org.mmmr.services.Conflict(
file.path,
mc,
pack,
mod
)
FROM MCFile file
LEFT OUTER JOIN file.mc mc
LEFT OUTER JOIN file.resource resource
LEFT OUTER JOIN resource.mod mod
LEFT OUTER JOIN resource.modPack pack
WHERE file.path=?