# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding field 'Party.unique_code'
        db.add_column(u'myapp_party', 'unique_code',
                      self.gf('django.db.models.fields.CharField')(default=0, max_length=6),
                      keep_default=False)


    def backwards(self, orm):
        # Deleting field 'Party.unique_code'
        db.delete_column(u'myapp_party', 'unique_code')


    models = {
        u'myapp.party': {
            'Meta': {'object_name': 'Party'},
            'host': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['myapp.User']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'unique_code': ('django.db.models.fields.CharField', [], {'max_length': '6'})
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
            'last_login': ('django.db.models.fields.DateField', [], {'null': 'True'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '30'})
        }
    }

    complete_apps = ['myapp']