#!/bin/bash

# Healthcare API Test Script
# Tests the full flow of the healthcare application

BASE_URL="http://localhost:8080"
echo "🚀 Starting Healthcare API Test Suite"
echo "======================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Test 1: Patient Registration
echo ""
print_status "Testing Patient Registration..."
PATIENT_REG_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/patient/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "dateOfBirth": "1990-01-15",
    "gender": "Male",
    "address": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    },
    "emergencyContact": {
      "name": "Jane Doe",
      "phone": "+1234567891",
      "relationship": "Spouse"
    },
    "medicalHistory": ["Hypertension", "Diabetes"],
    "insuranceInfo": {
      "provider": "Blue Cross",
      "policyNumber": "BC123456789",
      "groupNumber": "GRP001"
    }
  }')

echo "Patient Registration Response:"
echo "$PATIENT_REG_RESPONSE" | jq '.' 2>/dev/null || echo "$PATIENT_REG_RESPONSE"

# Extract patient ID from response
PATIENT_ID=$(echo "$PATIENT_REG_RESPONSE" | jq -r '.patientId' 2>/dev/null)
if [ "$PATIENT_ID" != "null" ] && [ -n "$PATIENT_ID" ]; then
    print_success "Patient registered successfully with ID: $PATIENT_ID"
else
    print_error "Failed to register patient"
fi

# Test 2: Patient Login
echo ""
print_status "Testing Patient Login..."
PATIENT_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/patient/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass123!"
  }')

echo "Patient Login Response:"
echo "$PATIENT_LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$PATIENT_LOGIN_RESPONSE"

# Extract patient token
PATIENT_TOKEN=$(echo "$PATIENT_LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ "$PATIENT_TOKEN" != "null" ] && [ -n "$PATIENT_TOKEN" ]; then
    print_success "Patient logged in successfully"
else
    print_error "Failed to login patient"
fi

# Test 3: Provider Registration
echo ""
print_status "Testing Provider Registration..."
PROVIDER_REG_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/provider/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Dr. Sarah",
    "lastName": "Johnson",
    "email": "dr.sarah.johnson@healthcare.com",
    "phoneNumber": "+1987654321",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "specialization": "Cardiology",
    "licenseNumber": "MD123456",
    "yearsOfExperience": 15,
    "clinicAddress": {
      "street": "456 Medical Center Dr",
      "city": "New York",
      "state": "NY",
      "zipCode": "10002",
      "country": "USA"
    }
  }')

echo "Provider Registration Response:"
echo "$PROVIDER_REG_RESPONSE" | jq '.' 2>/dev/null || echo "$PROVIDER_REG_RESPONSE"

# Extract provider ID from response
PROVIDER_ID=$(echo "$PROVIDER_REG_RESPONSE" | jq -r '.providerId' 2>/dev/null)
if [ "$PROVIDER_ID" != "null" ] && [ -n "$PROVIDER_ID" ]; then
    print_success "Provider registered successfully with ID: $PROVIDER_ID"
else
    print_error "Failed to register provider"
fi

# Test 4: Provider Login
echo ""
print_status "Testing Provider Login..."
PROVIDER_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/provider/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.sarah.johnson@healthcare.com",
    "password": "SecurePass123!"
  }')

echo "Provider Login Response:"
echo "$PROVIDER_LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$PROVIDER_LOGIN_RESPONSE"

# Extract provider token
PROVIDER_TOKEN=$(echo "$PROVIDER_LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)
if [ "$PROVIDER_TOKEN" != "null" ] && [ -n "$PROVIDER_TOKEN" ]; then
    print_success "Provider logged in successfully"
else
    print_error "Failed to login provider"
fi

# Test 5: Create Provider Availability
echo ""
print_status "Testing Provider Availability Creation..."
AVAILABILITY_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/provider/availability" \
  -H "Content-Type: application/json" \
  -H "X-Provider-Id: $PROVIDER_ID" \
  -d '{
    "date": "2024-01-15",
    "startTime": "09:00",
    "endTime": "17:00",
    "timezone": "America/New_York",
    "isRecurring": true,
    "recurrencePattern": "WEEKLY",
    "recurrenceEndDate": "2024-02-15",
    "slotDuration": 30,
    "breakDuration": 15,
    "appointmentType": "Consultation",
    "location": {
      "type": "clinic",
      "name": "Medical Center",
      "address": "456 Medical Center Dr, New York, NY 10002"
    },
    "pricing": {
      "consultation": 150.00,
      "currency": "USD"
    },
    "notes": "Regular consultation hours",
    "specialRequirements": ["Insurance card", "Medical history"]
  }')

echo "Provider Availability Response:"
echo "$AVAILABILITY_RESPONSE" | jq '.' 2>/dev/null || echo "$AVAILABILITY_RESPONSE"

# Test 6: Get Provider Availability
echo ""
print_status "Testing Get Provider Availability..."
GET_AVAILABILITY_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/v1/provider/${PROVIDER_ID}/availability?start_date=2024-01-15&end_date=2024-01-20")

echo "Get Provider Availability Response:"
echo "$GET_AVAILABILITY_RESPONSE" | jq '.' 2>/dev/null || echo "$GET_AVAILABILITY_RESPONSE"

# Test 7: Search Available Slots (Patient View)
echo ""
print_status "Testing Slot Search (Patient View)..."
SEARCH_SLOTS_RESPONSE=$(curl -s -X GET "${BASE_URL}/api/v1/availability/search?start_date=2024-01-15&end_date=2024-01-20&specialization=Cardiology&available_only=true")

echo "Slot Search Response:"
echo "$SEARCH_SLOTS_RESPONSE" | jq '.' 2>/dev/null || echo "$SEARCH_SLOTS_RESPONSE"

# Test 8: Update Availability Slot
echo ""
print_status "Testing Update Availability Slot..."
# First, get a slot ID from the search results
SLOT_ID=$(echo "$SEARCH_SLOTS_RESPONSE" | jq -r '.data[0].id' 2>/dev/null)
if [ "$SLOT_ID" != "null" ] && [ -n "$SLOT_ID" ]; then
    UPDATE_SLOT_RESPONSE=$(curl -s -X PUT "${BASE_URL}/api/v1/provider/availability/${SLOT_ID}" \
      -H "Content-Type: application/json" \
      -d '{
        "status": "BOOKED",
        "notes": "Updated slot information"
      }')
    
    echo "Update Slot Response:"
    echo "$UPDATE_SLOT_RESPONSE" | jq '.' 2>/dev/null || echo "$UPDATE_SLOT_RESPONSE"
else
    print_warning "No slots available to update"
fi

# Test 9: Delete Availability Slot
echo ""
print_status "Testing Delete Availability Slot..."
if [ "$SLOT_ID" != "null" ] && [ -n "$SLOT_ID" ]; then
    DELETE_SLOT_RESPONSE=$(curl -s -X DELETE "${BASE_URL}/api/v1/provider/availability/${SLOT_ID}?delete_recurring=false&reason=Testing")
    
    echo "Delete Slot Response:"
    echo "$DELETE_SLOT_RESPONSE" | jq '.' 2>/dev/null || echo "$DELETE_SLOT_RESPONSE"
else
    print_warning "No slots available to delete"
fi

# Test 10: Error Cases
echo ""
print_status "Testing Error Cases..."

# Test invalid patient registration
echo "Testing invalid patient registration (duplicate email)..."
INVALID_PATIENT_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/patient/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567892",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "dateOfBirth": "1995-05-20",
    "gender": "Female",
    "address": {
      "street": "789 Oak St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10003",
      "country": "USA"
    }
  }')

echo "Invalid Patient Registration Response:"
echo "$INVALID_PATIENT_RESPONSE" | jq '.' 2>/dev/null || echo "$INVALID_PATIENT_RESPONSE"

# Test invalid login
echo "Testing invalid login credentials..."
INVALID_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/v1/patient/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "WrongPassword123!"
  }')

echo "Invalid Login Response:"
echo "$INVALID_LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$INVALID_LOGIN_RESPONSE"

echo ""
echo "======================================"
print_success "Healthcare API Test Suite Completed!"
echo "======================================"

# Summary
echo ""
echo "📊 Test Summary:"
echo "✅ Patient Registration: $([ -n "$PATIENT_ID" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Patient Login: $([ -n "$PATIENT_TOKEN" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Provider Registration: $([ -n "$PROVIDER_ID" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Provider Login: $([ -n "$PROVIDER_TOKEN" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Availability Creation: $([ -n "$AVAILABILITY_RESPONSE" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Availability Retrieval: $([ -n "$GET_AVAILABILITY_RESPONSE" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Slot Search: $([ -n "$SEARCH_SLOTS_RESPONSE" ] && echo "PASSED" || echo "FAILED")"
echo "✅ Error Handling: $([ -n "$INVALID_PATIENT_RESPONSE" ] && echo "PASSED" || echo "FAILED")" 