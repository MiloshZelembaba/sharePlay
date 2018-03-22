# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'User.address'
        db.add_column(u'myapp_user', 'address',
                      self.gf('django.db.models.fields.CharField')(default='nothing yet', max_length=100),
                      keep_default=False)

        # Adding field 'User.port'
        db.add_column(u'myapp_user', 'port',
                      self.gf('django.db.models.fields.IntegerField')(default=0),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'User.address'
        db.delete_column(u'myapp_user', 'address')

        # Deleting field 'User.port'
        db.delete_column(u'myapp_user', 'port')


    models = {
        u'myapp.party': {
            'Meta': {'object_name': 'Party'},
            'host': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.User']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'unique_code': ('django.db.models.fields.CharField', [], {'max_length': '6'})
        },
        u'myapp.song': {
            'Meta': {'object_name': 'Song'},
            'artists': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'image_url': ('django.db.models.fields.CharField', [], {'max_length': '1000'}),
            'party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']"}),
            'song_name': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'spotify_uri': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'vote_count': ('django.db.models.fields.IntegerField', [], {})
        },
        u'myapp.user': {
            'Meta': {'object_name': 'User'},
            'address': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'current_party': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.Party']", 'null': 'True'}),
            'email': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last_login': ('django.db.models.fields.DateField', [], {'null': 'True'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'port': ('django.db.models.fields.IntegerField', [], {})
        }
    }

    complete_apps = ['myapp']