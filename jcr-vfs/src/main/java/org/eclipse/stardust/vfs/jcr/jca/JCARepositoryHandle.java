/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.stardust.vfs.jcr.jca;

// Copied from org.apache.jackrabbit.jca.JCARepositoryHandle.
// Modified to use JackrabbitRepositoryMCF instead of JCAManagedConnectionFactory.

import java.io.Serializable;

import javax.jcr.*;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.apache.jackrabbit.jca.JCAConnectionRequestInfo;

/**
 * This class implements the JCA implementation of repository.
 *
 * @author sauer
 * @version $Revision$
 */
public final class JCARepositoryHandle implements Repository, Referenceable, Serializable
{

   private static final long serialVersionUID = 1L;

   /**
    * Managed connection factory.
    */
   private final JackrabbitRepositoryMCF mcf;

   /**
    * Connection manager.
    */
   private final ConnectionManager cm;

   /**
    * Reference.
    */
   private Reference reference;

   /**
    * Construct the repository.
    */
   public JCARepositoryHandle(JackrabbitRepositoryMCF mcf, ConnectionManager cm)
   {
      this.mcf = mcf;
      this.cm = cm;
   }

   /**
    * Creates a new session.
    */
   public Session login() throws LoginException, NoSuchWorkspaceException,
         RepositoryException
   {
      return login(null, null);
   }

   /**
    * Creates a new session.
    */
   public Session login(Credentials creds) throws LoginException,
         NoSuchWorkspaceException, RepositoryException
   {
      return login(creds, null);
   }

   /**
    * Creates a new session.
    */
   public Session login(String workspace) throws LoginException,
         NoSuchWorkspaceException, RepositoryException
   {
      return login(null, workspace);
   }

   /**
    * Creates a new session.
    */
   public Session login(Credentials creds, String workspace) throws LoginException,
         NoSuchWorkspaceException, RepositoryException
   {
      return login(new JCAConnectionRequestInfo(creds, workspace));
   }

   /**
    * Creates a new session.
    */
   private Session login(JCAConnectionRequestInfo cri) throws LoginException,
         NoSuchWorkspaceException, RepositoryException
   {
      try
      {
         return (Session) cm.allocateConnection(mcf, cri);
      }
      catch (ResourceException e)
      {
         Throwable cause = e.getCause();

         if (cause instanceof LoginException)
         {
            throw (LoginException) cause;
         }
         else if (cause instanceof NoSuchWorkspaceException)
         {
            throw (NoSuchWorkspaceException) cause;
         }
         else if (cause instanceof RepositoryException)
         {
            throw (RepositoryException) cause;
         }
         else if (cause != null)
         {
            throw new RepositoryException(cause);
         }
         else
         {
            throw new RepositoryException(e);
         }
      }
   }

   /**
    * Return the descriptor keys.
    */
   public String[] getDescriptorKeys()
   {
      return mcf.getRepository().getDescriptorKeys();
   }

   /**
    * Return the descriptor for key.
    */
   public String getDescriptor(String key)
   {
      return mcf.getRepository().getDescriptor(key);
   }

   /**
    * Return the reference.
    */
   public Reference getReference()
   {
      return reference;
   }

   /**
    * Set the reference.
    */
   public void setReference(Reference reference)
   {
      this.reference = reference;
   }

   public Value getDescriptorValue(String key)
   {
      return mcf.getRepository().getDescriptorValue(key);
   }

   public Value[] getDescriptorValues(String key)
   {
      return mcf.getRepository().getDescriptorValues(key);
   }

   public boolean isSingleValueDescriptor(String key)
   {
      return mcf.getRepository().isSingleValueDescriptor(key);
   }

   public boolean isStandardDescriptor(String key)
   {
      return mcf.getRepository().isStandardDescriptor(key);
   }
}
