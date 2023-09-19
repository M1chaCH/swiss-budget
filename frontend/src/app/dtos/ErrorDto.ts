export type ErrorDto = {
  errorKey: "MailProviderNotSupportedException" | "MailConnectionException" | "DtoValidationException",
  args: any,
}
