curl --location --request PUT 'https://us-south.functions.cloud.ibm.com/api/v1/namespaces/_/actions/noOp?overwrite=true' \
--header 'X-Namespace-Id: 34f9adfd-d4c1-4674-ae2d-ae772a0f967e' \
--header 'Authorization: Bearer eyJraWQiOiIyMDIyMDExNjA4MjIiLCJhbGciOiJSUzI1NiJ9.eyJpYW1faWQiOiJJQk1pZC02NjQwMDM2RDlYIiwiaWQiOiJJQk1pZC02NjQwMDM2RDlYIiwicmVhbG1pZCI6IklCTWlkIiwic2Vzc2lvbl9pZCI6IkMtNWI0OTkwZjAtMjc3Ni00Y2I1LTlmMTMtNzk2ODdlMzNhZWIxIiwianRpIjoiNGFkOGE5ZjEtYzI1ZC00YTdmLWE1MjgtMDZlNmEyZmI5MWRkIiwiaWRlbnRpZmllciI6IjY2NDAwMzZEOVgiLCJnaXZlbl9uYW1lIjoiU29oaWwiLCJmYW1pbHlfbmFtZSI6IlNoYWgiLCJuYW1lIjoiU29oaWwgU2hhaCIsImVtYWlsIjoiamVuLm5ldHdvcmtAYXBwZ2FsbGFicy5pbyIsInN1YiI6Implbi5uZXR3b3JrQGFwcGdhbGxhYnMuaW8iLCJhdXRobiI6eyJzdWIiOiJqZW4ubmV0d29ya0BhcHBnYWxsYWJzLmlvIiwiaWFtX2lkIjoiSUJNaWQtNjY0MDAzNkQ5WCIsIm5hbWUiOiJTb2hpbCBTaGFoIiwiZ2l2ZW5fbmFtZSI6IlNvaGlsIiwiZmFtaWx5X25hbWUiOiJTaGFoIiwiZW1haWwiOiJqZW4ubmV0d29ya0BhcHBnYWxsYWJzLmlvIn0sImFjY291bnQiOnsiYm91bmRhcnkiOiJnbG9iYWwiLCJ2YWxpZCI6dHJ1ZSwiYnNzIjoiNzIxMWIzNTc3MDgwNDI3OTgzMjFjM2MwYzcxM2RlYmEifSwiaWF0IjoxNjQ0MjYxMjYwLCJleHAiOjE2NDQyNjI0NjAsImlzcyI6Imh0dHBzOi8vaWFtLmNsb3VkLmlibS5jb20vaWRlbnRpdHkiLCJncmFudF90eXBlIjoicGFzc3dvcmQiLCJzY29wZSI6ImlibSBvcGVuaWQiLCJjbGllbnRfaWQiOiJieCIsImFjciI6MSwiYW1yIjpbInB3ZCJdfQ.xjw_TqYfh_pfSbhKXiDGhRjA5d4PqZG1DFKeyl3MXPW6umQfr8viTX1UMPCdkUC8WvBK7XtjS9PuNUbpJSuYCR8fnL0EyyBP4cJLWITz-Z7qBhKVWWQ5neCH5E7DwAdCMNX84pJpjmmgK5arLpf_OWItTyd4SsM3PVzSMSUaxfHnWiuuaIurth42saZ1b2jxfo3ggJz3FZAz2b9VcuMg7Hke1sDYZ0Z9Yh9pH7M3LYHGjn65SmSZJMaK82ZdoZKx26eh4QKQnJ67tZSokC2zDH9bzRh--ZIa-f_7uKFHOfMeUoKsraG4FxNqQm9v4zohyngkXSPt6AIV9KspxEC2Iw' \
--header 'Content-Type: application/json' \
--data-raw '{"namespace":"_","name":"noOp","exec":{"kind":"blackbox","code":"def main(args):\n    return {\"body\": \"NOP-UPDATED\"}","image":"slydogshah/action-python-v3.6-ai"}}'