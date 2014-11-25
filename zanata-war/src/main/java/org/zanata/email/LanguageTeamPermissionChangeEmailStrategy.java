package org.zanata.email;

import java.util.List;
import javax.mail.internet.InternetAddress;

import org.zanata.events.LanguageTeamPermissionChangedEvent;
import org.zanata.i18n.Messages;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.googlecode.totallylazy.collections.PersistentMap;
import lombok.RequiredArgsConstructor;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@RequiredArgsConstructor
public class LanguageTeamPermissionChangeEmailStrategy extends EmailStrategy {
    private final LanguageTeamPermissionChangedEvent changedEvent;
    private final Messages msgs;

    @Override
    public String getSubject(Messages msgs) {
        return msgs.format("jsf.email.languageteam.permission.Subject",
                changedEvent.getLanguage());
    }

    @Override
    public String getBodyResourceName() {
        return "org/zanata/email/templates/language_team_permission_changed.vm";
    }

    @Override
    public Optional<InternetAddress[]> getReplyToAddress() {
        return Optional.of(Addresses.getReplyTo(
                changedEvent.getChangedByEmail(),
                changedEvent.getChangedByName()));
    }

    @Override
    public PersistentMap<String, Object> makeContext(
            PersistentMap<String, Object> genericContext,
            InternetAddress[] toAddresses) {
        PersistentMap<String, Object> context = super.makeContext(
                genericContext, toAddresses);
        List<String> oldPermissions = Lists.newArrayList();
        if (changedEvent.numOfGrantedOldPermissions() == 0) {
            oldPermissions
                    .add(
                    msgs.get("jsf.email.languageteam.permission.old.notInTeam"));
        } else {
            transformPermissionToDescription(
                    changedEvent.getOldPermission(), oldPermissions);
        }

        List<String> newPermissions = Lists.newArrayList();
        if (changedEvent.numOfGrantedNewPermissions() == 0) {
            newPermissions.add(msgs
                    .get("jsf.email.languageteam.permission.new.notInTeam"));
        } else {
            transformPermissionToDescription(
                    changedEvent.getNewPermission(), newPermissions);
        }

        return context
                .insert("language", changedEvent.getLanguage())
                .insert("changedByName", changedEvent.getChangedByName())
                .insert("changedByEmail", changedEvent.getChangedByEmail())
                .insert("oldPermissions", oldPermissions)
                .insert("newPermissions", newPermissions)
                .insert("toName", toAddresses[0].getPersonal());
    }

    private void transformPermissionToDescription(
            List<Boolean> permissionList, List<String> permissionDescriptions) {
        if (changedEvent.translatorPermissionOf(
                permissionList)) {
            permissionDescriptions.add(
                    msgs.get("jsf.email.languageteam.permission.isTranslator"));
        }
        if (changedEvent.reviewerPermissionOf(
                permissionList)) {
            permissionDescriptions.add(
                    msgs.get("jsf.email.languageteam.permission.isReviewer"));
        }
        if (changedEvent.coordinatorPermissionOf(
                permissionList)) {
            permissionDescriptions
                    .add(
                    msgs.get("jsf.email.languageteam.permission.isCoordinator"));
        }
    }
}
