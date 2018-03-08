# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'User'
        db.create_table(u'myapp_user', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('first_name', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('last_name', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('email', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('current_party', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.Party'], null=True)),
        ))
        db.send_create_signal(u'myapp', ['User'])

        # Adding model 'Song'
        db.create_table(u'myapp_song', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('spotify_uri', self.gf('django.db.models.fields.CharField')(max_length=100)),
            ('party', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.Party'])),
            ('vote_count', self.gf('django.db.models.fields.IntegerField')()),
        ))
        db.send_create_signal(u'myapp', ['Song'])

        # Adding model 'Party'
        db.create_table(u'myapp_party', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('host', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['myapp.User'])),
        ))
        db.send_create_signal(u'myapp', ['Party'])


    def backwards(self, orm):
        # Deleting model 'User'
        db.delete_table(u'myapp_user')

        # Deleting model 'Song'
        db.delete_table(u'myapp_song')

        # Deleting model 'Party'
        db.delete_table(u'myapp_party')


    models = {
        u'myapp.party': {
            'Meta': {'object_name': 'Party'},
            'host': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.User']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '30'})
        },
        u'myapp.song': {
            'Meta': {'object_name': 'Song'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']"}),
            'spotify_uri': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'vote_count': ('django.db.models.fields.IntegerField', [], {})
        },
        u'myapp.user': {
            'Meta': {'object_name': 'User'},
            'current_party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']", 'null': 'True'}),
            'email': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30'})
        }
    }

    complete_apps = ['myapp']