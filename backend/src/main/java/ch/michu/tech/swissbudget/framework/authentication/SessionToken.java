package ch.michu.tech.swissbudget.framework.authentication;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionToken {

    protected Date issuedAt;
    protected Date expiresAt;
    protected UUID userId;
    protected String userAgent;
    protected String remoteAddress;
    protected boolean stay;
    protected UUID sessionId;
}
