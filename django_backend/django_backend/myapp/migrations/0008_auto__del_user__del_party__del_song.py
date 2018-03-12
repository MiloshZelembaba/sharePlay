# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Deleting model 'User'
        db.delete_table(u'myapp_user')

        # Deleting model 'Party'
        db.delete_table(u'myapp_party')

        # Deleting model 'Song'
        db.delete_table(u'myapp_song')


    def backwards(self, orm):
        # Adding model 'User'
        db.create_table(u'myapp_user', (
            ('last_name', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('first_name', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('current_party', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.Party'], null=True)),
            ('email', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('last_login', self.gf('django.db.models.fields.DateField')(null=True)),
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
        ))
        db.send_create_signal(u'myapp', ['User'])

        # Adding model 'Party'
        db.create_table(u'myapp_party', (
            ('host', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.User'])),
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=30)),
        ))
        db.send_create_signal(u'myapp', ['Party'])

        # Adding model 'Song'
        db.create_table(u'myapp_song', (
            ('party', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.Party'])),
            ('vote_count', self.gf('django.db.models.fields.IntegerField')()),
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('spotify_uri', self.gf('django.db.models.fields.CharField')(max_length=100)),
        ))
        db.send_create_signal(u'myapp', ['Song'])


    models = {
        
    }

    complete_apps = ['myapp']