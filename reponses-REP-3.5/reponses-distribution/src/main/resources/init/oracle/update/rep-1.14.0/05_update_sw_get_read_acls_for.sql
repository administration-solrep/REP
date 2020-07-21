CREATE OR REPLACE FUNCTION "SW_GET_READ_ACLS_FOR" (userid VARCHAR)
RETURN NX_STRING_TABLE
-- List read acl ids for a list of user/groups, using the cache
IS
  in_cache NUMBER;
  aclids NX_STRING_TABLE;
BEGIN
        select distinct a.acl_id BULK COLLECT INTO aclids FROM SW_ACLR_USERID_USER u, SW_ACLR_USER_ACLID a
        WHERE u.USERGROUP = a.USERGROUP  AND REGEXP_LIKE(a.acl_id,'*[A-Z0-9]*','i') AND u.USER_ID= userid;
        RETURN aclids;
END;
/
