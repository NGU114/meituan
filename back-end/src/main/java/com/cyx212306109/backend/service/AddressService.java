package com.cyx212306109.backend.service;

import com.cyx212306109.backend.auth.UserContext;
import com.cyx212306109.backend.common.BusinessException;
import com.cyx212306109.backend.dto.AddressDto;
import com.cyx212306109.backend.entity.UserAccount;
import com.cyx212306109.backend.entity.UserAddress;
import com.cyx212306109.backend.repository.UserAccountRepository;
import com.cyx212306109.backend.repository.UserAddressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserAccountRepository userAccountRepository;

    public AddressService(UserAddressRepository userAddressRepository, UserAccountRepository userAccountRepository) {
        this.userAddressRepository = userAddressRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<AddressDto.AddressResponse> listMyAddresses() {
        Long userId = UserContext.getRequired().id();
        return userAddressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressDto.AddressResponse create(AddressDto.CreateRequest request) {
        Long userId = UserContext.getRequired().id();
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));

        if (Boolean.TRUE.equals(request.defaultAddress())) {
            resetDefaultAddress(userId);
        }

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setContactName(request.contactName());
        address.setContactPhone(request.contactPhone());
        address.setDetailAddress(request.detailAddress());
        address.setDefaultAddress(Boolean.TRUE.equals(request.defaultAddress()));
        userAddressRepository.save(address);
        return toResponse(address);
    }

    @Transactional
    public void delete(Long addressId) {
        Long userId = UserContext.getRequired().id();
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BusinessException("地址不存在"));
        userAddressRepository.delete(address);
    }

    private void resetDefaultAddress(Long userId) {
        userAddressRepository.findByUserIdOrderByDefaultAddressDescIdDesc(userId)
                .forEach(address -> address.setDefaultAddress(false));
    }

    private AddressDto.AddressResponse toResponse(UserAddress address) {
        return new AddressDto.AddressResponse(
                address.getId(),
                address.getContactName(),
                address.getContactPhone(),
                address.getDetailAddress(),
                address.getDefaultAddress()
        );
    }
}
