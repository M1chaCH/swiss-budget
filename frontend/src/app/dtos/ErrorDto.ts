export type ErrorDto = {
  errorKey: "MailProviderNotSupportedException"
      | "MailConnectionException"
      | "DtoValidationException"
      | "UserAlreadyExistsException"
      | "AgentNotRegisteredException"
      | "InvalidMfaCodeException"
      | "InvalidSessionTokenException"
      | "LoginFromNewClientException"
      | "RemoteAddressNotPresentException"
      | "MailSendException"
      | "MailTemplateNotFoundException"
      | "LoginFailedException"
      | "ResourceNotFoundException"
      | "UnexpectedServerException"
      | "InvalidTransactionMailFormatException"
      | "UnexpectedDbException",
  args: any,
}
